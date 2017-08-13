package com.codelanx.aether.common.bot.async.mouse;

import com.codelanx.aether.common.Randomization;
import com.codelanx.commons.util.Reflections;
import com.runemate.game.api.hybrid.entities.details.Interactable;
import com.runemate.game.api.hybrid.input.Mouse;
import com.runemate.game.api.hybrid.local.hud.InteractablePoint;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public enum UserInput {

    INSTANCE,
    ;

    private static final long MIN_CLICK_MS = 100;
    private static final long TASK_SWITCH_DELAY = 400;
    private final List<InputTarget> queue = new LinkedList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final AtomicLong lastInputMs = new AtomicLong();
    private Class<? extends InputTarget> lastInputType = null;

    public void registerClick(Interactable obj) {
        obj.click();
    }

    public static boolean attempt() {
        InputTarget target = INSTANCE.getNextTarget();
        if (target == null) {
            return false;
        }
        if (target.isAttempted()) {
            //hover secondary target
            if (!target.isSuccessful()) {
                target.attempt(); //immediate re-attempt
                return true;
            }
            INSTANCE.lastInputMs.set(System.currentTimeMillis());
            InputTarget next = INSTANCE.getNextTarget(1);
            if (next != null) {
                INSTANCE.actOnTarget(target, true);
            }
            INSTANCE.lastInputType = target.getClass();
            INSTANCE.queue.remove(0);
        } else if (!target.isAttempting()) {
            INSTANCE.actOnTarget(target, false);
        }
        return true;
    }
    
    private static long getMinimumClick() {
        return MIN_CLICK_MS + Randomization.MIN_CLICK.getValue().longValue();
    }
    
    //hmmmmm
    public static Supplier<Boolean> runemateInput(Supplier<Boolean> inputter) {
        RunemateTarget tar = new RunemateTarget(inputter);
        Reflections.operateLock(INSTANCE.lock.writeLock(), () -> {
            INSTANCE.queue.add(new RunemateTarget(inputter));
        });
        return tar::waitOnSuccess;
    }
    
    //TODO: proper input scheduling
    private void actOnTarget(InputTarget target, boolean hover) {
        long delay = System.currentTimeMillis() - this.lastInputMs.get();
        if (!hover && this.lastInputType != target.getClass()) {
            //we've got an input type switch
            Randomization r = Randomization.TASK_SWITCHING_DELAY;
            if (delay <= (TASK_SWITCH_DELAY + r.getRandom(t -> t.nextInt(r.getValue().intValue())).intValue())) {
                return;
            }
        }
        if (target instanceof MouseTarget) {
            MouseTarget mouse = (MouseTarget) target;
            if (hover) {
                if (mouse.getEntity().isVisible()) {
                    //precise hover
                    Mouse.move(mouse.getEntity());
                } else {
                    //let's kick the radius up via a rough npc oval
                    //get our raw interaction point
                    InteractablePoint point = mouse.getEntity().getInteractionPoint();
                    //TODO:
                    //InteractablePoint center;
                    //if we were able to get the center point here, it'd be GREAT.
                    //but alas we can't, so we resort to dirty dirty hacks
                    //
                    //get a hint as to a movement from our correct point to another correct point
                    InteractablePoint hint = mouse.getEntity().getInteractionPoint(point);
                    //now we reflect it:
                    hint.setLocation(
                            point.getX() + (point.getX() - hint.getX()),
                            point.getY() + (point.getY() - hint.getY()));
                    //and now move to our incorrect location
                    Mouse.move(hint);
                }
            } else {
                if (delay > UserInput.getMinimumClick()) {
                    mouse.attempt();
                }
                /*
                if (mouse.getType() == ClickType.SIMPLE) {
                    //schedule short
                } else {
                    //slightly longer scheduling
                }*/
            }
        } else if (!hover && !target.isAttempted()) { //TODO: Delay accounting
            target.attempt();
            //TODO: Keyboard handling, blerghehhg
        }
    }

    public static void interact(Interactable obj, String value) {
        Reflections.operateLock(INSTANCE.lock.writeLock(), () -> {
            INSTANCE.queue.add(new MouseTarget(obj, value));
        });
    }

    public static void click(Interactable obj) {
        Reflections.operateLock(INSTANCE.lock.writeLock(), () -> {
            INSTANCE.queue.add(new MouseTarget(obj));
        });
    }
    
    public static void chatInput(String input, boolean enter) {
        Reflections.operateLock(INSTANCE.lock.writeLock(), () -> {
            INSTANCE.queue.add(new KeyboardTarget(input, enter));
        });
    }

    public static boolean hasTasks() {
        return Reflections.operateLock(INSTANCE.lock.readLock(), () -> {
            return !INSTANCE.queue.isEmpty();
        });
    }
    
    private InputTarget getNextTarget() {
        return this.getNextTarget(0);
    }

    private InputTarget getNextTarget(int offset) {
        return Reflections.operateLock(INSTANCE.lock.readLock(), () -> {
            return this.queue.size() > offset ? null : this.queue.get(offset);
        });
    }
}
