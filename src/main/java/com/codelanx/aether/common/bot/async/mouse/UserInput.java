package com.codelanx.aether.common.bot.async.mouse;

import com.codelanx.commons.util.Reflections;
import com.runemate.game.api.hybrid.entities.details.Interactable;
import com.runemate.game.api.hybrid.input.Keyboard;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public enum UserInput {

    INSTANCE,
    ;

    private final List<InputTarget> queue = new LinkedList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

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
            InputTarget next = INSTANCE.getNextTarget(1);
            if (next != null) {
                actOnTarget(target, true);
            }
        } else if (!target.isAttempting()) {
            actOnTarget(target, false);
        }
        return true;
    }
    
    //TODO: proper input scheduling
    private static void actOnTarget(InputTarget target, boolean hover) {
        if (target instanceof MouseTarget) {
            MouseTarget mouse = (MouseTarget) target;
            if (mouse.getEntity().isVisible()) {
                //precise hover
            } else {
                //let's kick the radius up via a rough npc oval
            }
            if (mouse.getType() == ClickType.SIMPLE) {
                //schedule short
            } else {
                //slightly longer scheduling
            }
        } else {
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
