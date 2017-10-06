package com.codelanx.aether.common.bot.task;

import com.codelanx.aether.common.bot.Invalidator;
import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.Invalidators;
import com.codelanx.commons.logging.Logging;
import com.codelanx.aether.common.bot.task.predict.BranchPredictor;
import com.codelanx.commons.util.Reflections;
import com.codelanx.commons.util.Scheduler;
import com.runemate.game.api.hybrid.Environment;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class AetherTask<T> {

    public static AetherTask<?> NOTHING = AetherTask.of(() -> {});
    private static final Map<Invalidator, AetherTask<?>> invalidators = new HashMap<>();
    private final Map<HashedTaskState<T>, AetherTask<?>> children = new HashMap<>();
    private final Map<Predicate<T>, AetherTask<?>> pickyKids = new LinkedHashMap<>();
    private final BranchPredictor<T> predictor;
    private volatile CompletableFuture<T> state;
    private volatile CompletableFuture<Invalidator> execution;

    public AetherTask() {
        this.predictor = new BranchPredictor<T>();
    }

    public abstract Supplier<T> getStateNow();

    public final AetherTask<?> validate() {
        return children.get(new HashedTaskState<>(this.getStateNow().get()));
    }

    public AetherTask<?> getChild(T state) {
        AetherTask<?> back = this.children.get(new HashedTaskState<>(state));
        if (back == null) {
            back = this.pickyKids.entrySet().stream().filter(e -> {
                return e.getKey().test(state);
            }).findFirst().map(Map.Entry::getValue).orElseGet(() -> {
                return (AetherTask) AetherTask.this.children.get(HashedTaskState.DEFAULT); //fucking java generics
            });
        }
        return back;
    }

    public final boolean isStateRetrieved() {
        CompletableFuture<T> state = this.getState();
        return state.isDone();
    }

    public boolean isSync() {
        return false;
    }

    public final CompletableFuture<T> getState() {
        CompletableFuture<T> back;
        if ((back = stateOverride()) != null) {
            return back;
        }
        if (this.state == null) {
            CompletableFuture<T> state = this.register();
            if (this.state == null) {
                this.state = state;
            }
        }
        return this.state;
    }

    CompletableFuture<T> stateOverride() {
        return null;
    }

    private CompletableFuture<T> register() {
        if (this.isSync()) {
            return CompletableFuture.completedFuture(this.getStateNow().get());
        } else {
            return Scheduler.complete(this.getStateNow());
        }
    }

    public Class<?> getToken() {
        return this.getClass();
    }

    public void registerImmediate() {
        Aether.getBot().getBrain().getLogicTree().registerImmediate(this);
    }

    public final void invalidate() {
        if (this.state != null) {
            this.state = null;
            this.onInvalidate();
        }
    }

    public String getTaskName() {
        return this.getToken().getSimpleName();
    }

    protected void onInvalidate() {
        Logging.info("Invalidating: " + this.getClass().getSimpleName());
    }

    public AetherTask<?> getChild() throws ExecutionException, InterruptedException {
        try {
            T state = this.getState().get();
            AetherTask<?> back = this.getChild(state);
            Logging.info("Returning child (state: " + state + ", child: " + Optional.ofNullable(back).map(AetherTask::getTaskName).orElse(null) + ")");
            return back;
        } catch (Throwable t) {
            Logging.info("Sneaky ass exception");
            Logging.info(Reflections.stackTraceToString(t));
            throw t;
        }
    }

    public void forceInvalidate() {
        this.invalidate();
        this.getChildren().forEach(AetherTask::forceInvalidate);
    }

    public boolean isExecutable(T state) {
        return false;
    }

    public boolean isExecutable() {
        CompletableFuture<T> state = this.state; //non-blocking but potentially incorrect
        if (state == null || state.isCancelled() || state.isCompletedExceptionally()) {
            return false;
        }
        try {
            return this.isExecutable(state.get());
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }

    public static <E> AetherTask<E> of(Runnable task) {
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
            public Class<?> getToken() {
                return task.getClass();
            }

            @Override
            public Invalidator execute(E state) {
                task.run();
                return Invalidators.ALL;
            }
        };
    }

    public static <E> AetherTask<E> of(Invalidator invalidator) {
        return (AetherTask<E>) AetherTask.invalidators.computeIfAbsent(invalidator, i -> AetherTask.of(() -> i));
    }

    public static <E> AetherTask<E> of(Supplier<Invalidator> task) {
        return AetherTask.of(() -> null, state -> task.get());
    }

    public static <E> AetherTask<E> of(Supplier<E> state, Function<E, Invalidator> task) {
        return new AetherTask<E>() {
            @Override
            public Supplier<E> getStateNow() {
                return state;
            }

            @Override
            public boolean isExecutable() {
                return true;
            }

            @Override
            public Class<?> getToken() {
                return task.getClass();
            }

            @Override
            public Invalidator execute(E state) {
                return task.apply(state);
            }
        };
    }

    //since these tasks utilize their parent's state, we save an evaluation as well
    public static <E> AetherTask<E> of(AetherTask<E> derivedState, Function<E, Invalidator> task) {
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
            CompletableFuture<E> stateOverride() {
                return derivedState.getState();
            }


            @Override
            public Class<?> getToken() {
                return task.getClass();
            }

            @Override
            public Invalidator execute(E state) {
                return task.apply(state);
            }
        };
    }
    
    public <E> AetherTask<E> andThen(AetherTask<E> other) {
        if (!this.isExecutable() || !other.isExecutable()) {
            throw new UnsupportedOperationException("Cannot chain executions off a non-executable");
        }
        return new AetherTask<E>() {
            @Override
            public Supplier<E> getStateNow() {
                return () -> null;
            }

            @Override
            public boolean isExecutable() {
                return AetherTask.this.isExecutable();
            }

            @Override
            public Invalidator execute() {
                Invalidator fir = AetherTask.this.execute();
                Invalidator sec = other.execute();
                if (fir == Invalidators.ALL || sec == Invalidators.ALL) {
                    fir = Invalidators.ALL;
                } else if (fir != Invalidators.NONE && sec != Invalidators.NONE) {
                    fir = fir.getRange() > sec.getRange() ? fir : sec;
                } else {
                    fir = fir == Invalidators.NONE ? sec : fir;
                }
                return fir;
            }
        };
    }

    public Stream<AetherTask<?>> getChildren() {
        return this.children.values().stream();
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

    //this should be async
    public Invalidator execute() {
        T override = Optional.ofNullable(this.stateOverride()).map(comp -> comp.getNow(null)).orElse(null);
        if (override == null) {
            override = this.getStateNow().get();
        }
        return this.execute(override);
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

    protected void registerDefault(Invalidator invalidator) {
        this.registerDefault(() -> invalidator);
    }

    protected void registerDefault(Supplier<Invalidator> child) {
        this.registerDefault(AetherTask.of(child));
    }

    protected void registerDefault(Function<? super T, Invalidator> executor) {
        this.registerDefault(AetherTask.of(this, executor::apply));
    }

    //provides an invalidator to dictate branch invalidation
    protected void registerInvalidator(T key, Supplier<Invalidator> rangeInvalidated) {
        this.register(key, AetherTask.of(rangeInvalidated));
    }

    protected void registerInvalidator(Predicate<T> applicable, Supplier<Invalidator> child) {
        this.register(applicable, AetherTask.of(child));
    }

    protected void registerInvalidator(Predicate<T> applicable, Function<T, Invalidator> child) {
        this.pickyKids.put(applicable, AetherTask.of(this, child));
    }

    protected <E> void register(T key, AetherTask<E> child) {
        this.children.put(new HashedTaskState<>(key), child);
    }

    protected void register(T key, Runnable child) {
        this.register(key, AetherTask.of(child));
    }

    protected void register(Predicate<T> applicable, Runnable child) {
        this.register(applicable, AetherTask.of(child));
    }

    protected void register(Predicate<T> applicable, Supplier<Invalidator> child) {
        this.register(applicable, AetherTask.of(child));
    }

    protected void register(Predicate<T> applicable, Consumer<? super T> child) {
        this.register(applicable, state -> {
            child.accept(state);
            return Invalidators.SELF;
        });
    }

    protected void register(Predicate<T> applicable, Function<? super T, Invalidator> child) {
        this.register(applicable, AetherTask.of(this, child::apply));
    }

    protected void register(Predicate<T> applicable, Invalidator invalidator) {
        this.register(applicable, AetherTask.of(invalidator));
    }

    protected <E> void register(Predicate<T> applicable, AetherTask<E> child) {
        this.pickyKids.put(applicable, child);
    }

    //registers a child supplier for a boolean, which invalidates all previous branches
    //if it returns true
    protected void registerRunemateCall(T key, Supplier<Boolean> invalidateAll) {
        this.register(key, AetherTask.ofRunemateFailable(invalidateAll));
    }


    @Override
    public String toString() {
        return "AetherTask{" +
                "children=" + children +
                ", pickyKids=" + pickyKids +
                ", state=" + state +
                ", execution=" + execution +
                '}';
    }
}
