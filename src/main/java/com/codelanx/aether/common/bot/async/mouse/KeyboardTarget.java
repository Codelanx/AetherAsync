package com.codelanx.aether.common.bot.async.mouse;

import com.codelanx.aether.common.bot.async.Aether;
import com.codelanx.aether.common.bot.async.AetherAsyncBot;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by rogue on 8/13/2017.
 */
public class KeyboardTarget extends InputTarget {

    private final String input;
    private final boolean enter;
    private CompletableFuture<Boolean> entering = null;
    
    public KeyboardTarget(String input, boolean enter) {
        this.input = input;
        this.enter = enter;
    }
    
    @Override
    public void attempt() {
        this.entering = CompletableFuture.supplyAsync(() -> {
            return true;
        }, Aether.getScheduler().getThreadPool());
    }

    @Override
    public boolean isAttempting() {
        return this.entering != null;
    }

    @Override
    public boolean isAttempted() {
        return this.entering != null && (this.entering.isDone() || this.entering.isCompletedExceptionally());
    }

    @Override
    public boolean isSuccessful() {
        if (this.entering == null) {
            return false;
        }
        try {
            return this.entering.get();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }
}
