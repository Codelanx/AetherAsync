package com.codelanx.aether.common.input;

import com.codelanx.aether.common.Randomization;
import com.codelanx.aether.common.input.type.CombatTarget;
import com.codelanx.aether.common.input.type.KeyboardTarget;
import com.codelanx.aether.common.input.type.MouseTarget;
import com.codelanx.aether.common.input.type.RunemateTarget;
import com.codelanx.commons.logging.Logging;
import com.codelanx.commons.util.OptimisticLock;
import com.runemate.game.api.hybrid.entities.details.Interactable;
import com.runemate.game.api.hybrid.input.Mouse;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public enum UserInput {

    INSTANCE,
    ;

    private static final long MIN_CLICK_MS = 100;
    private static final long TASK_SWITCH_DELAY = 300;
    private final List<InputTarget> queue = new LinkedList<>();
    private final OptimisticLock lock = new OptimisticLock();
    private final AtomicLong lastInputMs = new AtomicLong(); //last successfully entered input
    private final AtomicLong lastInputTargetMs = new AtomicLong(); //last ms mark for input (can be in future)
    private Class<? extends InputTarget> lastInputType = null;

    public void registerClick(Interactable obj) {
        obj.click();
    }

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
            //TODO: Move off bot thread
            if (hover) {
                mouse.hover();
            } else if (delay > UserInput.getMinimumClick()) {
                mouse.attempt();
            }
        } else if (!hover && !target.isAttempting()) {
            target.attempt();
        }
    }

    public InputTarget getNextTarget() {
        return this.getNextTarget(0);
    }

    public InputTarget getNextTarget(int offset) {
        return INSTANCE.lock.read(() -> this.queue.size() <= offset ? null : this.queue.get(offset));
    }

    // -=- bot methods

    public static boolean attempt() {
        //Logging.info("Running user input...");
        InputTarget target = INSTANCE.getNextTarget();
        if (target == null) {
            Logging.info("Null next target");
            return false;
        }
        if (target.isAttempted()) {
            //hover secondary target
            if (!target.isSuccessful()) {
                Logging.info("Re-attempting input");
                target.attempt(); //immediate re-attempt
                return true;
            }
            Logging.info("Input successful");
            INSTANCE.lastInputMs.set(System.currentTimeMillis());
            InputTarget next = INSTANCE.getNextTarget(1);
            if (next != null) {
                Logging.info("Hovering next input...");
                INSTANCE.actOnTarget(target, true);
            }
            INSTANCE.lastInputType = target.getClass();
            INSTANCE.queue.remove(0);
        } else if (!target.isAttempting()) {
            INSTANCE.actOnTarget(target, false);
        }
        return true;
    }

    public static long getMinimumClick() {
        double mult = 1 / Mouse.getSpeedMultiplier();
        return (long) ((MIN_CLICK_MS + Randomization.MIN_CLICK.getValue().longValue()) * mult);
    }

    public static long getInputIntervalDelay(Class<? extends InputTarget> targetType) {
        return 0L;//INSTANCE.getInterval(targetType);
    }

    public static boolean hasTasks() {
        return !INSTANCE.lock.read(INSTANCE.queue::isEmpty);
    }

    public static void wipe() {
        INSTANCE.lock.write(INSTANCE.queue::clear);
    }

    // -=- input methods

    //hmmmmm
    public static RunemateTarget runemateInput(Supplier<Boolean> inputter) {
        return UserInput.runemateInput(null, inputter);
    }

    public static RunemateTarget runemateInput(String debugDescription, Supplier<Boolean> inputter) {
        return UserInput.addTask(new RunemateTarget(debugDescription, inputter));
    }

    public static MouseTarget interact(Interactable obj, String value) {
        return UserInput.addTask(new MouseTarget(obj, value));
    }

    public static CombatTarget combat(Interactable obj) {
        return UserInput.addTask(new CombatTarget(obj));
    }

    public static MouseTarget click(Interactable obj) {
        return UserInput.addTask(new MouseTarget(obj));
    }

    public static KeyboardTarget type(String input, boolean enter) {
        return UserInput.addTask(new KeyboardTarget(input, enter));
    }

    private static <T extends InputTarget> T addTask(T target) {
        INSTANCE.lock.write(() -> INSTANCE.queue.add(target));
        return target;
    }

}
