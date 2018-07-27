package com.codelanx.aether.common.input;

import com.codelanx.aether.common.input.type.CombatTarget;
import com.codelanx.aether.common.input.type.KeyboardTarget;
import com.codelanx.aether.common.input.type.MouseTarget;
import com.codelanx.aether.common.input.type.RunemateTarget;
import com.codelanx.commons.util.OptimisticLock;
import com.runemate.game.api.hybrid.entities.details.Interactable;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public enum UserInput3 {

    INSTANCE,
    ;

    private static final long MIN_CLICK_MS = 100;
    private static final long TASK_SWITCH_DELAY = 300;
    private final LinkedList<NewInputTarget> queue = new LinkedList<>();
    private final OptimisticLock lock = new OptimisticLock();
    private final AtomicLong lastInputMs = new AtomicLong(); //last successfully entered input
    private final AtomicLong lastInputTargetMs = new AtomicLong(); //last ms mark for input (can be in future)

    //this one will be based on scheduling / queueing

    //TODO: revisit after cleaning up InputTarget
    public static void fire() {
        NewInputTarget tar = INSTANCE.getNextTarget();
        if (tar == null) {
            //nothing to fire
            return;
        }
        //let's try scheduling within here

    }



    //-=- instance methods

    //time between two different inputs
    private long getInterval(Class<? extends InputTarget> targetType) {
        long delay = System.currentTimeMillis() - this.lastInputMs.get();
        /*if (this.lastInputType != targetType) {
            //we've got an input type switch
            Randomization r = Randomization.TASK_SWITCHING_DELAY;
            return (TASK_SWITCH_DELAY + r.getRandom(t -> t.nextInt(r.getValue().intValue())).intValue());
        }*/
        return 0;
    }

    public NewInputTarget getNextTarget() {
        return this.getNextTarget(0);
    }

    public NewInputTarget getNextTarget(int offset) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be zero or positive");
        }
        return this.lock.read(() -> this.queue.size() <= offset ? null : this.queue.get(offset));
    }

    public NewInputTarget popTarget() {
        return this.lock.read(this.queue::pop);
    }

    public boolean hasTasks() {
        return !this.lock.read(this.queue::isEmpty);
    }

    //-=- input methods

    //hmmmmm
    public static RunemateTarget runemateInput(Supplier<Boolean> inputter) {
        return UserInput.runemateInput(null, inputter);
    }

    public static RunemateTarget runemateInput(String debugDescription, Supplier<Boolean> inputter) {
        return UserInput3.addTask(new RunemateTarget(debugDescription, inputter));
    }

    public static MouseTarget interact(Interactable obj, String value) {
        return UserInput3.addTask(new MouseTarget(obj, value));
    }

    public static CombatTarget combat(Interactable obj) {
        return UserInput3.addTask(new CombatTarget(obj));
    }

    public static MouseTarget click(Interactable obj) {
        return UserInput3.addTask(new MouseTarget(obj));
    }

    public static KeyboardTarget type(String input, boolean enter) {
        return UserInput3.addTask(new KeyboardTarget(input, enter));
    }

    private static <T extends InputTarget> T addTask(T target) {
        INSTANCE.lock.write(() -> INSTANCE.queue.add((NewInputTarget) (Object) target)); //TODO: remove type mask (when finished rewrite)
        //TODO: Schedule task
        return target;
    }

    // -=- old bot methods
/*
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
        return INSTANCE.getInterval(targetType);
    }

    public static boolean hasTasks() {
        return Parallel.operateLock(INSTANCE.lock.readLock(), () -> {
            return !INSTANCE.queue.isEmpty();
        });
    }

    public static void wipe() {
        Parallel.operateLock(INSTANCE.lock.writeLock(), INSTANCE.queue::clear);
    }*/
}
