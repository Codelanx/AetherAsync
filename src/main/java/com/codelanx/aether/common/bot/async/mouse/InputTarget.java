package com.codelanx.aether.common.bot.async.mouse;

import com.codelanx.aether.common.bot.async.Aether;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * Created by rogue on 8/13/2017.
 */
public abstract class InputTarget {
    
    private CompletableFuture<Boolean> attempt;
    
    protected void doAttempt(Supplier<Boolean> task) {
        this.attempt = CompletableFuture.supplyAsync(task, Aether.getScheduler().getThreadPool());
    }
    
    public abstract void attempt();

    public boolean isAttempting() {
        return this.attempt != null;
    }
    
    public boolean isAttempted() {
        return this.attempt != null && (this.attempt.isDone() || this.attempt.isCompletedExceptionally());
    }
    public boolean isSuccessful() {
        if (this.attempt == null) {
            return false;
        }
        try {
            return this.attempt.get();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }
    
    //erhggugh
    //this is ugly, we need a different way
    @Deprecated
    public boolean waitOnSuccess() {
        while (!this.isAttempted()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this.isSuccessful();
    }
}
