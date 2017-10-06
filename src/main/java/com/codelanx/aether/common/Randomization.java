package com.codelanx.aether.common;

import com.codelanx.commons.config.MemoryConfig;
import com.codelanx.commons.util.Reflections;
import com.runemate.game.api.hybrid.player_sense.PlayerSense;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;

public enum Randomization implements MemoryConfig<Number> {

    //EXAMPLE("some-key", () -> ThreadLocalRandom.current().nextDouble(0.25D, 0.5D)),
    
    WPM("wpm", () -> (ThreadLocalRandom.current().nextDouble() * 70) + 80), // WPM 80-150
    //keep in mind most of these are variances, not actual end results
    TASK_SWITCHING_DELAY("task_switch_delay_ceiling", () -> ThreadLocalRandom.current().nextInt(50, 100)),
    //associative to WPM
    //[0,30] - [-35,35]
    //lower wpm equals a larger delay in minimum click rate
    //end results: [-35,65] range for minimum click (applied over +100 ms)
    MIN_CLICK("min_click", () -> (ThreadLocalRandom.current().nextGaussian() * 30) - (WPM.getValue().doubleValue() - 112)),
    INPUT_TYPE_SWITCH_DELAY("input_switch_delay_ceiling", () -> ThreadLocalRandom.current().nextInt(200)),
    //how many times are we willing to left click if it withdraws all the necessary items
    LEFT_CLICK_WITHDRAW_TOLERANCE("left_click_withdraw_tolerance", () -> ThreadLocalRandom.current().nextInt(1, 3)) //TODO: bell curve? this is a pretty small range
    ;

    private final String key;
    private Supplier<Number> setter;

    private Randomization(String key, Supplier<Number> setter) {
        this.key = key;
        this.setter = setter;
        this.regen();
    }

    public void regen() {
        this.setValue(this.setter.get());
    }

    @Override
    public void setValue(Number val) {
        PlayerSense.put(this.key, val);
    }

    public Number getRandom(Function<ThreadLocalRandom, Number> num) {
        return this.getValue().doubleValue() * Reflections.convertNumber(num.apply(ThreadLocalRandom.current()), double.class);
    }
    
    @Override
    public Number getValue() {
        return (Number) PlayerSense.get(this.key); //could also #as, but we've specified Number generically
    }
}
