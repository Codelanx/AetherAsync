package com.codelanx.aether.common.input;

import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.AetherCompletableFuture;
import com.codelanx.commons.util.Reflections;
import com.codelanx.commons.util.Scheduler;
import com.runemate.game.api.hybrid.Environment;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Created by rogue on 8/13/2017.
 */
public abstract class InputTarget {

    private static final int MAX_ATTEMPTS = 5;
    private CompletableFuture<Boolean> attempt;
    private CompletableFuture<Boolean> postAttempt = new AetherCompletableFuture<>();
    private final AtomicInteger attempts = new AtomicInteger();

    protected void doAttempt(Supplier<Boolean> task) {
        Environment.getLogger().info("Registered input task (" + Scheduler.getTaskCount() + ")");
        ;
        if (this.attempts.incrementAndGet() > MAX_ATTEMPTS) {
            throw new UserInputException("Couldn't successfully run input task");
        }
        this.attempt = Scheduler.complete(task);
        this.attempt.whenComplete((value, ex) -> {
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
        } catch (ExecutionException e) {
            Environment.getLogger().severe("Error while attempting user input");
            Environment.getLogger().severe(Reflections.stackTraceToString(e));
            return false;
        } catch (InterruptedException e) {
            Environment.getLogger().severe("Error while attempting user input");
            Environment.getLogger().severe(Reflections.stackTraceToString(e));
            Aether.getBot().stop();
            return false;
        }
    }
}
