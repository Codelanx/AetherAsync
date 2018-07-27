package com.codelanx.aether.common.input.type;

import com.codelanx.aether.common.input.ClickType;
import com.codelanx.aether.common.input.NewInputTarget;
import com.runemate.game.api.hybrid.entities.details.Interactable;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.hud.InteractablePoint;

import java.util.function.Supplier;

public class NewMouseTarget extends NewInputTarget {
    private final Interactable target;
    private final String action;
    //private final boolean menu;

    public NewMouseTarget(Interactable target, long delay) {
        this(target, null, delay);
    }

    public NewMouseTarget(Interactable target, String action, long delay) {
        //this(target, action, false);
        super(delay);
        if (target == null) {
            throw new IllegalArgumentException("Cannot click a null object");
        }
        this.target = target;
        this.action = action;
    }

    /*public MouseTarget(Interactable target, String action, boolean menu) {
        this.target = target;
        this.action = action;
        this.menu = menu;
    }*/

    //TODO: ingrained support for this based on the main logic tree being delayed
    public void hover() {
        if (this.target.isVisible()) {
            //precise hover
            Mouse.move(this.target);
        } else {
            //let's kick the radius up via a rough npc oval
            //get our raw interaction point
            InteractablePoint point = this.target.getInteractionPoint();
            //TODO:
            //InteractablePoint center;
            //if we were able to get the center point here, it'd be GREAT.
            //but alas we can't, so we resort to dirty dirty hacks
            //
            //get a hint as to a movement from our correct point to another correct point
            InteractablePoint hint = this.target.getInteractionPoint(point);
            //now we reflect it:
            hint.setLocation(
                    point.getX() + (point.getX() - hint.getX()),
                    point.getY() + (point.getY() - hint.getY()));
            //and now move to our incorrect location
            Mouse.move(hint);
        }
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
    public Supplier<Boolean> getAction() {
        return this.action == null
                ? this.target::click
                : () -> this.target.interact(this.action);
    }

    @Override
    public String toString() {
        return "MouseTarget{" +
                "action='" + action + '\'' +
                ", target=" + target +
                '}';
    }
}
