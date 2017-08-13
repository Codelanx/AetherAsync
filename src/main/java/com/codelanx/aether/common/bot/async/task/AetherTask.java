package com.codelanx.aether.common.bot.async.task;

import com.codelanx.aether.common.bot.async.AetherAsyncBot;
import com.codelanx.aether.common.bot.async.Invalidator;
import com.codelanx.aether.common.bot.async.Invalidators;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class AetherTask<T> {

    public static AetherTask<?> NOTHING = AetherTask.of(() -> {});
    private final Map<HashedTaskState<T>, AetherTask<?>> children = new HashMap<>();
    private final Map<Predicate<T>, AetherTask<?>> pickyKids = new LinkedHashMap<>();
    private volatile CompletableFuture<T> state;

    public abstract Supplier<T> getStateNow();

    public final AetherTask<?> validate() {
        return children.get(new HashedTaskState<>(this.getStateNow().get()));
    }

    public AetherTask<?> getChild(T state) {
        AetherTask<?> back = this.children.get(new HashedTaskState<>(state));
        if (back == null) {
            back = this.pickyKids.entrySet().stream().filter(e -> {
                return e.getKey().test(state);
            }).findFirst().map(Map.Entry::getValue).orElse(null);
        }
        return back;
    }

    public final boolean isStateRetrieved() {
        CompletableFuture<T> state = this.getState();
        return state.isDone();
    }

    public final CompletableFuture<T> getState() {
        if (this.state == null) {
            CompletableFuture<T> state = CompletableFuture.supplyAsync(this.getStateNow(), AetherAsyncBot.get().getScheduler().getThreadPool());
            if (this.state == null) {
                this.state = state;
            }
        }
        return this.state;
    }

    public final void invalidate() {
        this.state = null;
        this.onInvalidate();
    }

    protected void onInvalidate() {

    }

    public AetherTask<?> getChild() throws ExecutionException, InterruptedException {
        return this.getChild(this.getState().get());
    }

    public boolean isExecutable() {
        return false;
    }

    public static <E> AetherTask<E> of(Runnable task) {
        return AetherTask.of(() -> {
            task.run();
            return Invalidators.ALL;
        });
    }

    public static <E> AetherTask<E> of(Supplier<Invalidator> task) {
        return new AetherTask<E>() {
            @Override
            public Supplier<E> getStateNow() {
                return () -> null;
            }

            @Override
            public boolean isExecutable() {
                return true;
            }

            @Override
            public Invalidator execute(E state) {
                return task.get();
            }
        };
    }

    public static <E> AetherTask<E> ofRunemateFailable(Supplier<Boolean> task) {
        return new AetherTask<E>() {
            @Override
            public Supplier<E> getStateNow() {
                return () -> null;
            }

            @Override
            public boolean isExecutable() {
                return true;
            }

            @Override
            public Invalidator execute(E state) {
                return task.get() ? Invalidators.ALL : Invalidators.NONE;
            }
        };
    }

    //
    //
    //

    /**
     * return a result from Invalidators here to override any branch hinting
     * and manually attempt the specififed override
     *
     * note that Invalidators#invalidateNone may be ignored, null will not
     * override any results
     *
     * @return An {@link Invalidator} describing the branch invalidation
     */
    public Invalidator invalidationOverride() {
        return null;
    }

    public final boolean invalidateAnyway() {
        return this.invalidationOverride() != null && this.invalidationOverride().isAll();
    }

    public Invalidator execute() {
        return this.execute(this.getStateNow().get());
    }

    public Invalidator execute(T state) {
        throw new UnsupportedOperationException("Cannot execute a validation task");
    }

    protected <E> void registerDefault(AetherTask<E> child) {
        this.children.put(new HashedTaskState<>(null), child);
    }

    protected void registerDefault(Runnable child) {
        this.registerDefault(AetherTask.of(child));
    }

    protected void registerDefault(Supplier<Invalidator> child) {
        this.registerDefault(AetherTask.of(child));
    }

    //provides an invalidator to dictate branch invalidation
    protected void registerInvalidator(T key, Supplier<Invalidator> rangeInvalidated) {
        this.register(key, AetherTask.of(rangeInvalidated));
    }

    protected <E> void register(T key, AetherTask<E> child) {
        this.children.put(new HashedTaskState<>(key), child);
    }

    protected void register(T key, Runnable child) {
        this.register(key, AetherTask.of(child));
    }

    //registers a child supplier for a boolean, which invalidates all previous branches
    //if it returns true
    protected void registerRunemateCall(T key, Supplier<Boolean> invalidateAll) {
        this.register(key, AetherTask.ofRunemateFailable(invalidateAll));
    }

    protected <E> void register(Predicate<T> applicable, AetherTask<E> child) {
        this.pickyKids.put(applicable, child);
    }

}
