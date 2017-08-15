package com.codelanx.aether.common.bot;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by rogue on 8/14/2017.
 */
//TODO: Support the children futures made with this? they'll only fire after the first one
public class ProxiedCompletableFuture<E> extends CompletableFuture<E> {
    
    private final Executor exec;
    
    public ProxiedCompletableFuture(Executor exec) {
        this.exec = exec;
    }


    protected Executor getThreadPool() {
        return this.exec;
    }

    @Override
    public CompletableFuture<Void> acceptEitherAsync(CompletionStage<? extends E> other, Consumer<? super E> action) {
        return super.acceptEitherAsync(other, action, this.getThreadPool());
    }

    @Override
    public <U> CompletableFuture<U> applyToEitherAsync(CompletionStage<? extends E> other, Function<? super E, U> fn) {
        return super.applyToEitherAsync(other, fn, this.getThreadPool());
    }

    @Override
    public <U> CompletableFuture<U> handleAsync(BiFunction<? super E, Throwable, ? extends U> fn) {
        return super.handleAsync(fn, this.getThreadPool());
    }

    @Override
    public CompletableFuture<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
        return super.runAfterBothAsync(other, action, this.getThreadPool());
    }

    @Override
    public CompletableFuture<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
        return super.runAfterEitherAsync(other, action, this.getThreadPool());
    }

    @Override
    public CompletableFuture<Void> thenAcceptAsync(Consumer<? super E> action) {
        return super.thenAcceptAsync(action, this.getThreadPool());
    }

    @Override
    public <U> CompletableFuture<U> thenApplyAsync(Function<? super E, ? extends U> fn) {
        return super.thenApplyAsync(fn, this.getThreadPool());
    }

    @Override
    public <U, V> CompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super E, ? super U, ? extends V> fn) {
        return super.thenCombineAsync(other, fn, this.getThreadPool());
    }

    @Override
    public CompletableFuture<Void> thenRunAsync(Runnable action) {
        return super.thenRunAsync(action, this.getThreadPool());
    }

    @Override
    public CompletableFuture<E> whenCompleteAsync(BiConsumer<? super E, ? super Throwable> action) {
        return super.whenCompleteAsync(action, this.getThreadPool());
    }

}
