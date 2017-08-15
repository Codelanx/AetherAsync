package com.codelanx.aether.common.bot.input;

import com.runemate.game.api.hybrid.entities.details.Interactable;
import com.runemate.game.api.hybrid.input.Mouse;

public class MouseTarget extends InputTarget {
    
    private final Interactable target;
    private final String action;
    //private final boolean menu;

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
        this.doAttempt(this.action == null
                ? this.target::click
                : () -> this.target.interact(this.action));
    }
    
}
