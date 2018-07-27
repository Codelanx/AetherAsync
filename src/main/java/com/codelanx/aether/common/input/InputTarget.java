package com.codelanx.aether.common.input;

import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.AetherCompletableFuture;
import com.codelanx.commons.logging.Logging;
import com.codelanx.commons.util.Readable;
import com.codelanx.commons.util.Scheduler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Created by rogue on 8/13/2017.
 */
public abstract class InputTarget {

    private static final int MAX_ATTEMPTS = 5;
    private volatile CompletableFuture<Boolean> attempt;
    private CompletableFuture<Boolean> postAttempt = new AetherCompletableFuture<>();
    private final AtomicInteger attempts = new AtomicInteger();

    protected void doAttempt(Supplier<Boolean> task) {
        Logging.info("Starting user input (task-" + Scheduler.getTaskCount() + ": " + this.toString() + ")");
        if (this.attempts.incrementAndGet() >= MAX_ATTEMPTS) {
            UserInputException t = new UserInputException("Failed to run input task");
            this.postAttempt.completeExceptionally(t); //contract chaining
            throw t;
        }
        this.attempt = Scheduler.complete(task);
        this.attempt.whenComplete((value, ex) -> {
            if (ex == null && value) {
                this.postAttempt.complete(value); //true only, we only complete (in total) when successful
            }
        });
    }

    boolean doneMaxAttempts() {
        return this.attempts.get() + 1 >= MAX_ATTEMPTS;
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
        if (this.attempt.isCancelled()) {
            return false;
        }
        try {
            return this.attempt.get();
        } catch (ExecutionException e) {
            Logging.severe("Error while attempting user input");
            Logging.severe(Readable.stackTraceToString(e));
            return false;
        } catch (InterruptedException e) {
            Logging.severe("Error while attempting user input");
            Logging.severe(Readable.stackTraceToString(e));
            Aether.getBot().stop();
            return false;
        }
    }
}
