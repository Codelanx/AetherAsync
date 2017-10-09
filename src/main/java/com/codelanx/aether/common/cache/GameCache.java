package com.codelanx.aether.common.cache;

import com.codelanx.aether.common.cache.query.Inquiry;
import com.codelanx.commons.logging.Logging;
import com.codelanx.commons.util.Parallel;
import com.codelanx.commons.util.Reflections;
import com.codelanx.commons.util.Scheduler;
import com.runemate.game.api.hybrid.entities.details.Interactable;
import com.runemate.game.api.hybrid.queries.QueryBuilder;
import com.runemate.game.api.hybrid.queries.results.QueryResults;
import com.runemate.game.api.hybrid.util.Validatable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
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
    
    public Supplier<? extends QueryResults<T, ?>> getResults(Queryable<T, I> queryable) {
        return this.getResults(queryable.toInquiry());
    }

    public abstract Supplier<? extends QueryBuilder<T, ?, ?>> getRawQuery();

    public abstract QueryType getType();

    public boolean isEmpty() {
        return this.results.isEmpty();
    }
    
    //blocking, returns when completed
    protected final CacheHolder<T> compute(I inq) {
        Logging.info(this.getType().name() + "(cache)#compute(" + inq + ")");
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

    public abstract long getLifetimeMS();
    
    public final Stream<T> getCurrent(I inq) {
        CacheHolder<T> hold = this.compute(inq);
        if (this.getLifetimeMS() <= 0 || hold.getLastUpdateMS() + this.getLifetimeMS() > System.currentTimeMillis()) {
            hold.update(this, inq);
        }
        //uses a copy atm in case list changes
        return hold.getCopyList().stream(); //TODO: not copies, but something else
    }

    //validates as well
    public final Stream<T> get(I inq) {
        return this.getCurrent(inq).map(i -> {
            if (!(i instanceof Validatable) || ((Validatable) i).isValid()) {
                return i;
            } else {
                this.invalidate(inq, i);
                return null;
            }
        }).filter(Objects::nonNull);
    }

    public final Stream<T> get(Queryable<T, I> inq) {
        return this.get(inq.toInquiry());
    }
    
    public int size(I inq) {
        CacheHolder<T> hold = this.compute(inq);
        return Parallel.operateLock(hold.lock.readLock(), hold.getList()::size);
    }

    public final int size(Queryable<T, I> inq) {
        return this.size(inq.toInquiry());
    }

    public final void replaceFirst(I inq, UnaryOperator<T> replacement) {
        CacheHolder<T> hold = this.compute(inq);
        Parallel.operateLock(hold.getLock().writeLock(), () -> {
            List<T> vals = hold.getList();
            if (!vals.isEmpty()) {
                vals.set(0, replacement.apply(vals.get(0)));
            }
        });
    }

    public final void replaceFirst(Queryable<T, I> inq, UnaryOperator<T> replacement) {
        this.replaceFirst(inq.toInquiry(), replacement);
    }
    
    public final void invalidate(I inq, T item) {
        Parallel.operateLock(this.lock.writeLock(), () -> {
            CacheHolder<?> hold = this.results.getOrDefault(inq, CacheHolder.EMPTY);
            if (Parallel.operateLock(hold.lock.writeLock(), () -> hold.getList().remove(item))) {
                this.onInvalidate(inq, item);
            }
        });
    }

    public final void invalidate(Queryable<T, I> inq, T item) {
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

    public final List<T> invalidateByType(Queryable<T, I> inq) {
        return this.invalidateByType(inq.toInquiry());
    }

    protected void onInvalidate(I inq, T item) {

    }
    
    public final void invalidateAll() {
        Reflections.operateLock(this.lock.writeLock(), this.results::clear);
        this.onInvalidate(null, null);
    }
    
    //null if not present, otherwise current
    protected CacheHolder<T> getCurrentRaw(I inq) {
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

    
    private static class CacheHolder<T extends Interactable> {

        private static final CacheHolder EMPTY = new CacheHolder<>(Collections.emptyList());
        private final List<T> list = new ArrayList<>();
        private final AtomicLong lastUpdate = new AtomicLong(System.currentTimeMillis());
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        
        public CacheHolder(List<T> list) {
            this.list.addAll(list);
        }
        
        List<T> getList() {
            return this.list;
        }
        
        public List<T> getCopyList() {
            return Parallel.operateLock(this.lock.readLock(), () -> new ArrayList<>(this.list));
        }
        
        ReadWriteLock getLock() {
            return this.lock;
        }

        public <I extends Inquiry> void update(GameCache<T, I> cache, I inquiry) {
            List<T> fresh = cache.getResults(inquiry).get().asList();
            Parallel.operateLock(this.lock.writeLock(), () -> {
                this.list.clear();
                this.list.addAll(fresh);
            });
            this.lastUpdate.set(System.currentTimeMillis());
        }

        public long getLastUpdateMS() {
            return this.lastUpdate.get();
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
