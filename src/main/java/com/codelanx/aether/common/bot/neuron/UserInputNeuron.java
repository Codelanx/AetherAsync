package com.codelanx.aether.common.bot.neuron;

import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.Brain;
import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.input.NewInputTarget;
import com.codelanx.aether.common.input.UserInput;
import com.codelanx.aether.common.input.UserInput3;
import com.codelanx.aether.common.input.UserInputException;
import com.codelanx.commons.logging.Logging;
import com.codelanx.commons.util.Readable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public class UserInputNeuron extends Neuron {

    private static final int MAX_ATTEMPTS = 5;

    private final AtomicReference<CompletableFuture<Boolean>> currentTask = new AtomicReference<>();

    @Override
    public boolean applies() {
        return UserInput.hasTasks();
    }

    @Override
    public void fire(Brain brain) {
        CompletableFuture<Boolean> curr = this.currentTask.get();
        CompletableFuture<Boolean> set = curr;
        NewInputTarget next = UserInput3.INSTANCE.getNextTarget();
        try {
            if (curr == null || curr.isCancelled()) {
                //schedule new task
                set = next.attempt();
            } else {
                //next == "current task"
                if (curr.isDone() || curr.isCompletedExceptionally()) {
                    try {
                        if (curr.get()) {
                            //success
                        } else if (next.getAttempts() >= MAX_ATTEMPTS) {
                            //failure
                            throw new UserInputException("Failed to run input task: max retries exceeded (" + MAX_ATTEMPTS + ")");
                        } else {
                            //retry
                            set = next.attempt();
                        }
                    } catch (ExecutionException e) {
                        Logging.severe("Error while attempting user input");
                        Logging.severe(Readable.stackTraceToString(e));
                        return;
                    } catch (InterruptedException e) {
                        Logging.severe("Error while attempting user input");
                        Logging.severe(Readable.stackTraceToString(e));
                        Aether.getBot().stop();
                        return;
                    }
                }
                if (curr.isCancelled() || curr.isDone() || curr.isCompletedExceptionally()) {
                    //curr =
                }
            }
            this.currentTask.compareAndSet(curr, set);
        } catch (UserInputException ex) {
            Logging.log(Level.SEVERE, "Error while attempting user input, invalidating bot and retrying...", ex);
            Aether.getBot().getBrain().getLogicTree().invalidate();
            UserInput.wipe();
            Caches.invalidateAll();
        }
    }

    public void fireOld(Brain brain) {
        try {
            UserInput.attempt();
        } catch (UserInputException ex) {
            Logging.log(Level.SEVERE, "Error while attempting user input, invalidating bot and retrying...", ex);
            Aether.getBot().getBrain().getLogicTree().invalidate();
            UserInput.wipe();
            Caches.invalidateAll();
        }
    }

    @Override
    public boolean isBlocking() {
        return UserInput.hasTasks();
    }
}
