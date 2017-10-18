package com.codelanx.aether.common.bot.neuron;

import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.Brain;
import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.input.NewInputTarget;
import com.codelanx.aether.common.input.UserInput;
import com.codelanx.aether.common.input.UserInput3;
import com.codelanx.aether.common.input.UserInputException;
import com.codelanx.aether.common.input.type.NewMouseTarget;
import com.codelanx.commons.logging.Logging;
import com.codelanx.commons.util.Readable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public class UserInputNeuron extends Neuron {

    private final AtomicReference<CompletableFuture<Boolean>> currentTask = new AtomicReference<>();

    @Override
    public boolean applies() {
        return UserInput.hasTasks();
    }

    public void fireNew(Brain brain) {
        CompletableFuture<Boolean> curr = this.currentTask.get();
        CompletableFuture<Boolean> set = curr;
        NewInputTarget next;
        try {
            if (curr == null) {
                next = UserInput3.INSTANCE.getNextTarget();
            } else if (curr.isCancelled() || curr.isDone()) {
                UserInput3.INSTANCE.popTarget();
                next = UserInput3.INSTANCE.getNextTarget();
            } else if (curr.isCompletedExceptionally()) {
                //TODO: invalidate bot
                curr.handle((b, t) -> {
                    Logging.log(Level.SEVERE, "Error while attempting user input, invalidating bot and retrying...");
                    Aether.getBot().getBrain().getLogicTree().invalidate();
                    UserInput.wipe();
                    Caches.invalidateAll();
                    return null;
                });
                return;
            } else {
                //we have a running input atm
                next = UserInput3.INSTANCE.getNextTarget();
                if (next instanceof NewMouseTarget) {
                    //TODO: find a new mousetarget and hover it after completion
                    //also note that this would be solely with none or keyboard input inbetween, not runemate ones
                }
                next = null;
            }
            if (next == null) {
                //no input to be fired
                return;
            }
            if (next != null) { //return anyhow, this disables the below code without tipping off IntelliJ
                return;
            }


            if (curr == null || curr.isCancelled()) {
                //schedule new task
                next = UserInput3.INSTANCE.getNextTarget(1);
                if (next != null) {
                    set = next.start();
                }
            } else {
                //next == "current task"
                if (curr.isDone() || curr.isCompletedExceptionally()) {
                    try {
                        if (curr.get()) {
                            //success
                        } else if (next.getAttempts() >= next.getMaxAttempts()) {
                            //failure
                            throw new UserInputException("Failed to run input task: max retries exceeded (" + next.getMaxAttempts() + ")");
                        } else {
                            //retry
                            set = next.start();
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

    @Override
    public void fire(Brain brain) {
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
