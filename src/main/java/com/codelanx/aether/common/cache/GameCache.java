package com.codelanx.aether.common.cache;

import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.cache.query.Inquiry;
import com.codelanx.commons.logging.Logging;
import com.codelanx.commons.util.Reflections;
import com.codelanx.commons.util.Scheduler;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.details.Interactable;
import com.runemate.game.api.hybrid.queries.QueryBuilder;
import com.runemate.game.api.hybrid.queries.results.QueryResults;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Created by rogue on 8/14/2017.
 */
//calls to caches will block until correct info is available. Try to avoid main bot thread
public abstract class GameCache<T extends Interactable, I extends Inquiry> {
    
    private final Map<I, CacheHolder<T>> results = new HashMap<>();
    private final Map<I, CompletableFuture<List<T>>> queries = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReadWriteLock queryLock = new ReentrantReadWriteLock();
    
    public abstract Supplier<? extends QueryResults<T, ?>> getResults(I inquiry);
    
    public abstract Supplier<? extends QueryBuilder<T, ?, ?>> getRawQuery();
    
    //blocking, returns when completed
    protected final CacheHolder<T> compute(I inq) {
        Logging.info("#compute(" + inq + ")");
        CacheHolder<T> back = Reflections.operateLock(this.lock.readLock(), () -> this.results.get(inq));
        if (back == null) {
            CompletableFuture<List<T>> query = this.getQuery(inq); //TODO: Thread safety
            try {
                CacheHolder<T> res = new CacheHolder<>(query.get());
                Scheduler.runAsyncTask(() -> {
                    Logging.info("Inserting results: " + res);
                    Reflections.operateLock(this.lock.writeLock(), () -> this.results.put(inq, res));
                    Reflections.operateLock(this.queryLock.writeLock(), () -> this.queries.remove(inq));
                });
                Logging.info("Returning new results: " + res);
                return res;
            } catch (ExecutionException | InterruptedException e) {
                Logging.info("Cache query interrupted / Error querying for information:");
                Logging.info(Reflections.stackTraceToString(e));
                Reflections.operateLock(this.lock.writeLock(), () -> this.results.remove(inq));
                Reflections.operateLock(this.queryLock.writeLock(), () -> this.queries.remove(inq));
                throw new RuntimeException("Cache failed to load", e);
            }
        }
        Logging.info("Returning cached results: " + back);
        return back;
    }
    
    public final Stream<T> get(I inq) {
        CacheHolder<T> hold = this.compute(inq);
        //uses a copy atm in case list changes
        return Reflections.operateLock(hold.lock.readLock(), hold::getCopyList).stream(); //TODO: not copies, but something else
    }

    public final Stream<T> get(Queryable<I> inq) {
        return this.get(inq.toInquiry());
    }
    
    public int size(I inq) {
        CacheHolder<T> hold = this.compute(inq);
        return Reflections.operateLock(hold.lock.readLock(), hold.getList()::size);
    }

    public final int size(Queryable<I> inq) {
        return this.size(inq.toInquiry());
    }
    
    public final void replaceFirst(I inq, UnaryOperator<T> replacement) {
        CacheHolder<T> hold = this.compute(inq);
        Reflections.operateLock(hold.getLock().writeLock(), () -> {
            List<T> vals = hold.getList();
            if (!vals.isEmpty()) {
                vals.set(0, replacement.apply(vals.get(0)));
            }
        });
    }
    
    public final void invalidate(I inq, T item) {
        Reflections.operateLock(this.lock.writeLock(), () -> {
            CacheHolder<?> hold = this.results.getOrDefault(inq, CacheHolder.EMPTY);
            if (Reflections.operateLock(hold.lock.writeLock(), () -> hold.getList().remove(item))) {
                this.onInvalidate(inq, item);
            }
        });
    }

    public final void invalidate(Queryable<I> inq, T item) {
        this.invalidate(inq.toInquiry(), item);
    }
    
    public final List<T> invalidateByType(I inq) {
        CacheHolder<T> back = Reflections.operateLock(this.lock.writeLock(), () -> {
            return this.results.remove(inq);
        });
        if (back != null) {
            this.onInvalidate(inq, null);
            return back.getList();
        }
        return null;
    }

    public final List<T> invalidateByType(Queryable<I> inq) {
        return this.invalidateByType(inq.toInquiry());
    }

    protected void onInvalidate(I inq, T item) {

    }
    
    public final void invalidateAll() {
        Reflections.operateLock(this.lock.writeLock(), this.results::clear);
        this.onInvalidate(null, null);
    }
    
    //null if not present, otherwise current
    protected CacheHolder<T> getCurrent(I inq) {
        return Reflections.operateLock(this.lock.readLock(), () -> this.results.get(inq));
    }
    
    protected final CompletableFuture<List<T>> getQuery(I inq) {
        return Reflections.operateLock(this.queryLock.writeLock(), () -> this.queries.computeIfAbsent(inq, this::schedule));
    }
    
    private CompletableFuture<List<T>> schedule(I inq) {
        return Scheduler.complete(this.getResults(inq)).thenApply(QueryResults::asList).thenApply(this::convertList);
    }
    
    protected List<T> convertList(List<T> raw) {
        return raw;
    }

    
    private static class CacheHolder<T> {

        private static final CacheHolder EMPTY = new CacheHolder(Collections.emptyList());
        private final List<T> list = new ArrayList<>();
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        
        public CacheHolder(List<T> list) {
            this.list.addAll(list);
        }
        
        public List<T> getList() {
            return this.list;
        }
        
        public List<T> getCopyList() {
            return new ArrayList<>(this.list);
        }
        
        public ReadWriteLock getLock() {
            return this.lock;
        }

        @Override
        public String toString() {
            return Reflections.operateLock(this.lock.readLock(), () -> {
                return "CacheHolder{" +
                        "list=" + list +
                        '}';
            });
        }
    }
}
