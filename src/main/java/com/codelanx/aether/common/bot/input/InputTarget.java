package com.codelanx.aether.common.bot.input;

import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.AetherCompletableFuture;
import com.codelanx.commons.util.Reflections;
import com.runemate.game.api.hybrid.Environment;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * Created by rogue on 8/13/2017.
 */
public abstract class InputTarget {
    
    private CompletableFuture<Boolean> attempt;
    private CompletableFuture<Boolean> postAttempt = new AetherCompletableFuture<>();
    
    protected void doAttempt(Supplier<Boolean> task) {
        this.attempt = CompletableFuture.supplyAsync(task, Aether.getScheduler().getThreadPool());
        this.attempt.whenCompleteAsync((value, ex) -> {
            if (ex == null && value) {
                this.postAttempt.complete(value); //true only, we only complete (in total) when successful
            }
        });
    }
    
    public CompletableFuture<Boolean> postAttempt() {
        return this.postAttempt;
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
            Environment.getLogger().severe("Error while attempting user input");
            Environment.getLogger().severe(Reflections.stackTraceToString(e));
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
