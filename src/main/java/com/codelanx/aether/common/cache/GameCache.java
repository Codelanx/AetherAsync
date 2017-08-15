package com.codelanx.aether.common.cache;

import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.cache.query.Inquiry;
import com.codelanx.commons.util.Reflections;
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
public abstract class GameCache<T extends Interactable, I extends Inquiry> {
    
    private final Map<I, CacheHolder<T>> results = new HashMap<>();
    private final Map<I, CompletableFuture<List<T>>> queries = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReadWriteLock queryLock = new ReentrantReadWriteLock();
    
    public abstract Supplier<? extends QueryResults<T, ?>> getResults(I inquiry);
    
    public abstract Supplier<? extends QueryBuilder<T, ?, ?>> getRawQuery();
    
    //blocking, returns when completed
    protected final CacheHolder<T> compute(I inq) {
        CacheHolder<T> back = Reflections.operateLock(this.lock.readLock(), () -> this.results.get(inq));
        if (back == null) {
            CompletableFuture<List<T>> query = this.getQuery(inq);
            try {
                CacheHolder<T> res = new CacheHolder<>(query.get());
                Aether.getScheduler().getThreadPool().execute(() -> {
                    Reflections.operateLock(this.lock.writeLock(), () -> this.results.put(inq, res));
                    Reflections.operateLock(this.queryLock.writeLock(), () -> this.queries.remove(inq));
                });
                return res;
            } catch (InterruptedException | ExecutionException e) {
                Environment.getLogger().info("Error querying for information:");
                Environment.getLogger().info(Reflections.stackTraceToString(e));
            }
        }
        return back;
    }
    
    public final Stream<T> get(I inq) {
        CacheHolder<T> hold = this.compute(inq);
        return Reflections.operateLock(hold.lock.readLock(), hold::getCopyList).stream(); //TODO: not copies, but something else
    }
    
    public final int size(I inq) {
        return this.compute(inq).getList().size();
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
            CacheHolder<?> hold = this.results.getOrDefault(inq, EMPTY);
            Reflections.operateLock(hold.lock.writeLock(), () -> {
                hold.getList().remove(item);
            });
        });
    }
    
    public final void invalidateByType(I inq) {
        Reflections.operateLock(this.lock.writeLock(), () -> {
            this.results.remove(inq);
        });
    }
    
    public final void invalidateAll() {
        Reflections.operateLock(this.lock.writeLock(), this.results::clear);
    }
    
    //null if not present, otherwise current
    protected CacheHolder<T> getCurrent(I inq) {
        return Reflections.operateLock(this.lock.readLock(), () -> this.results.get(inq));
    }
    
    protected final CompletableFuture<List<T>> getQuery(I inq) {
        return Reflections.operateLock(this.queryLock.writeLock(), () -> this.queries.computeIfAbsent(inq, this::schedule));
    }
    
    private CompletableFuture<List<T>> schedule(I inq) {
        return CompletableFuture.supplyAsync(this.getResults(inq), Aether.getScheduler().getThreadPool()).thenApply(QueryResults::asList).thenApply(this::convertList);
    }
    
    protected List<T> convertList(List<T> raw) {
        return raw;
    }
    
    private static final CacheHolder EMPTY = new CacheHolder(Collections.emptyList());
    
    private static class CacheHolder<T> {
        
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
    }
}
