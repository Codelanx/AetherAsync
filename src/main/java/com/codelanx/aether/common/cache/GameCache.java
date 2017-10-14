package com.codelanx.aether.common.cache;

import com.codelanx.aether.common.cache.query.Inquiry;
import com.codelanx.commons.logging.Logging;
import com.codelanx.commons.util.OptimisticLock;
import com.codelanx.commons.util.Parallel;
import com.codelanx.commons.util.Readable;
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
    private final OptimisticLock lock = new OptimisticLock();
    private final OptimisticLock queryLock = new OptimisticLock();
    
    public abstract Supplier<? extends QueryResults<T, ?>> getRunemateResults(I inquiry);
    
    public Supplier<? extends QueryResults<T, ?>> getRunemateResults(Queryable<T, I> queryable) {
        return this.getRunemateResults(queryable.toInquiry());
    }

    public abstract Supplier<? extends QueryBuilder<T, ?, ?>> getRawQuery();

    public abstract QueryType getType();

    public boolean isEmpty() {
        return this.results.isEmpty();
    }
    
    //blocking, returns when completed
    protected final CacheHolder<T> compute(I inq) {
        Logging.info(this.getType().name() + "(cache)#compute(" + inq + ")");
        CacheHolder<T> back = this.lock.read(() -> this.results.get(inq));
        if (back == null) {
            CompletableFuture<List<T>> query = this.getQuery(inq); //TODO: Thread safety
            try {
                CacheHolder<T> res = new CacheHolder<>(query.get());
                Scheduler.runAsyncTask(() -> {
                    Logging.info("Inserting results: " + res);
                    this.lock.write(() -> this.results.put(inq, res));
                    this.queryLock.write(() -> this.queries.remove(inq));
                });
                Logging.info("Returning new results: " + res);
                return res;
            } catch (ExecutionException | InterruptedException e) {
                Logging.severe("Cache query interrupted / Error querying for information:");
                Logging.severe(Readable.stackTraceToString(e));
                this.lock.write(() -> this.results.remove(inq));
                this.queryLock.write(() -> this.queries.remove(inq));
                throw new RuntimeException("Cache failed to load", e);
            }
        }
        Logging.info("Returning cached results: " + back);
        return back;
    }

    //lifetime of a cached object in milliseconds
    public abstract long getLifetimeMS();

    //gets a copy of the currently held cached value, and updates it if necessary
    public final Stream<T> getCurrent(I inq) {
        CacheHolder<T> hold = this.compute(inq);
        if (this.getLifetimeMS() <= 0 || hold.getLastUpdateMS() + this.getLifetimeMS() > System.currentTimeMillis()) {
            hold.update(this, inq);
        }
        //uses a copy atm in case list changes
        return hold.getCopyList().stream(); //TODO: not copies, but something else
    }

    //validates as well
    //gets current cache values, invalidating old results if applicable
    public final Stream<T> get(I inq) {
        Stream<T> held = this.getCurrent(inq);
        if (this.compute(inq).getLastUpdateMS() + 20 < System.currentTimeMillis()) { //if not updated within the last 20 ms
            held = held.map(i -> {
                if (!(i instanceof Validatable) || ((Validatable) i).isValid()) {
                    return i;
                } else {
                    this.invalidate(inq, i);
                    return null;
                }
            }).filter(Objects::nonNull);
        }
        return held;
    }

    public final Stream<T> get(Queryable<T, I> inq) {
        return this.get(inq.toInquiry());
    }
    
    public int size(I inq) {
        CacheHolder<T> hold = this.compute(inq);
        return hold.lock.read(hold.getList()::size);
    }

    public final int size(Queryable<T, I> inq) {
        return this.size(inq.toInquiry());
    }

    public final void replaceFirst(I inq, UnaryOperator<T> replacement) {
        CacheHolder<T> hold = this.compute(inq);
        hold.getLock().write(() -> {
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
        this.lock.write(() -> {
            CacheHolder<?> hold = this.results.getOrDefault(inq, CacheHolder.EMPTY);
            if (hold.lock.write(() -> hold.getList().remove(item))) {
                this.onInvalidate(inq, item);
            }
        });
    }

    public final void invalidate(Queryable<T, I> inq, T item) {
        this.invalidate(inq.toInquiry(), item);
    }
    
    public final List<T> invalidateByType(I inq) {
        CacheHolder<T> back = this.lock.write(() -> this.results.remove(inq));
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
        this.lock.write(this.results::clear);
        this.onInvalidate(null, null);
    }
    
    //null if not present, otherwise current
    protected CacheHolder<T> getCurrentRaw(I inq) {
        return this.lock.read(() -> this.results.get(inq));
    }
    
    protected final CompletableFuture<List<T>> getQuery(I inq) {
        return this.queryLock.write(() -> this.queries.computeIfAbsent(inq, this::schedule));
    }
    
    protected CompletableFuture<List<T>> schedule(I inq) {
        return Scheduler.complete(this.getRunemateResults(inq)).thenApply(QueryResults::asList);
    }

    
    private static class CacheHolder<T extends Interactable> {

        private static final CacheHolder EMPTY = new CacheHolder<>(Collections.emptyList());
        private final List<T> list = new ArrayList<>();
        private final AtomicLong lastUpdate = new AtomicLong(System.currentTimeMillis());
        private final OptimisticLock lock = new OptimisticLock();
        
        public CacheHolder(List<T> list) {
            this.list.addAll(list);
        }
        
        List<T> getList() {
            return this.list;
        }
        
        public List<T> getCopyList() {
            return this.lock.read(() -> new ArrayList<>(this.list));
        }

        OptimisticLock getLock() {
            return this.lock;
        }

        public <I extends Inquiry> void update(GameCache<T, I> cache, I inquiry) {
            List<T> fresh = cache.getRunemateResults(inquiry).get().asList();
            this.lock.write(() -> {
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
            return this.lock.read(() ->
                    "CacheHolder{" +
                    "list=" + list +
                    '}');
        }
    }
}
