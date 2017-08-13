package com.codelanx.aether.common.bot.async.mouse;

import com.codelanx.aether.common.bot.async.Aether;
import com.codelanx.aether.common.bot.async.AetherAsyncBot;
import com.runemate.game.api.hybrid.entities.details.Interactable;
import com.runemate.game.api.hybrid.input.Mouse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class MouseTarget extends InputTarget {
    
    private final Interactable target;
    private final String action;
    //private final boolean menu;
    private CompletableFuture<Boolean> clicking = null;

    public MouseTarget(Interactable target) {
        this(target, null);
    }
    
    public MouseTarget(Interactable target, String action) {
        //this(target, action, false);
        this.target = target;
        this.action = action;
    }

    /*public MouseTarget(Interactable target, String action, boolean menu) {
        this.target = target;
        this.action = action;
        this.menu = menu;
    }*/

    public void hover() {
        Mouse.move(this.target);
    }
    
    public Interactable getEntity() {
        return this.target;
    }

    public ClickType getType() {
        //if (this.menu) {
            //return this.action == null ? ClickType.MENU_SIMPLE : ClickType.MENU_INTERACT;
        //}
        return this.action == null ? ClickType.SIMPLE : ClickType.INTERACT;
    }

    @Override
    public void attempt() {
        Supplier<Boolean> clicker = this.action == null
                ? this.target::click
                : () -> this.target.interact(this.action);
        this.clicking = CompletableFuture.supplyAsync(clicker, Aether.getScheduler().getThreadPool());
    }
    
    @Override
    public boolean isAttempting() {
        return this.clicking != null;
    }

    @Override
    public boolean isAttempted() {
        return this.clicking != null && (this.clicking.isDone() || this.clicking.isCompletedExceptionally());
    }

    @Override
    public boolean isSuccessful() {
        if (this.clicking == null) {
            return false;
        }
        try {
            return this.clicking.get();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }
}
