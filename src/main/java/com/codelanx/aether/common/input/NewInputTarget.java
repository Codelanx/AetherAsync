package com.codelanx.aether.common.input;

import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.AetherCompletableFuture;
import com.codelanx.commons.logging.Logging;
import com.codelanx.commons.util.Reflections;
import com.codelanx.commons.util.Scheduler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public abstract class NewInputTarget {

    private static final int MAX_ATTEMPTS = 5;
    private volatile CompletableFuture<Boolean> attempt;
    private final AtomicInteger attempts = new AtomicInteger();
    private final AtomicBoolean started = new AtomicBoolean();

    public abstract Supplier<Boolean> getAction();

    public int getMaxAttempts() {
        return MAX_ATTEMPTS;
    }

    public int getAttempts() {
        return this.attempts.get();
    }

    boolean doneMaxAttempts() {
        return this.attempts.get() >= this.getMaxAttempts();
    }

    public CompletableFuture<Boolean> getFuture() {
        return this.attempt;
    }

    public CompletableFuture<Boolean> start() {
        if (!this.started.compareAndSet(false, true)) {
            throw new UnsupportedOperationException("Input targets cannot be manually restarted");
        }
        return this.doAttempt(this.getAction());
    }

    protected CompletableFuture<Boolean> doAttempt(Supplier<Boolean> task) {
        CompletableFuture<Boolean> last = this.attempt;
        if (this.attempt != null || this.started.get()) {
            throw new UnsupportedOperationException("Input targets cannot be manually restarted");
        }
        int max = this.getMaxAttempts();
        if (max <= 0) {
            throw new IllegalStateException("Max number of attempts must be a positive number (input: " + max + ")");
        }
        if (this.attempts.incrementAndGet() > max) {
            this.attempts.decrementAndGet();
            UserInputException t = new UserInputException("Maximum input retries exceeded (" + max + ")");
            last.completeExceptionally(t); //contract chaining
            throw t;
        }
        Logging.info("Starting user input (task-" + Scheduler.getTaskCount() + ": " + this.toString() + ")");
        this.attempt = this.buildAttempt(task);
        return this.attempt;
    }

    private CompletableFuture<Boolean> buildAttempt(Supplier<Boolean> task) {
        CompletableFuture<Boolean> attempt = this.rawAttempt(task);
        this.attempts.incrementAndGet();
        int max = this.getMaxAttempts();
        if (max <= 0) {
            throw new IllegalStateException("Max number of attempts must be a positive number (input: " + max + ")");
        }
        for (int i = 0; i < max; i++) {
            if (attempt.isCompletedExceptionally()) {
                break;
            }
            CompletableFuture<Boolean> fattempt = attempt;
            attempt = attempt.exceptionally(t -> {
                if (!(t instanceof UserInputException)) {
                    throw (RuntimeException) t;
                }
                if (this.attempts.incrementAndGet() > max) { //shouldn't happen, but if someone hacked around a rerun this'll fuck em over a little bit
                    this.attempts.decrementAndGet();
                    UserInputException ex = new UserInputException("Maximum input retries exceeded (" + max + ")");
                    fattempt.obtrudeException(ex);
                    throw ex;
                }
                Logging.warning("Task failed, retrying... [task: " + this + "]");
                return this.rawAttempt(task).join();
            });
        }
        return attempt;
    }

    private CompletableFuture<Boolean> rawAttempt(Supplier<Boolean> task) {
        return Scheduler.complete(task).thenApply(b -> {
            if (!b) {
                throw new UserInputException("Failed to run input (false returned)");
            }
            return b;
        });
    }

    //TODO: Remove



    public CompletableFuture<Boolean> postAttempt() {
        return this.getFuture();
    }


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
            Logging.severe(Reflections.stackTraceToString(e));
            return false;
        } catch (InterruptedException e) {
            Logging.severe("Error while attempting user input");
            Logging.severe(Reflections.stackTraceToString(e));
            Aether.getBot().stop();
            return false;
        }
    }
}
