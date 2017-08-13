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
    TASK_SWITCHING_DELAY("task_switch_delay_ceiling", () -> ThreadLocalRandom.current().nextInt(50, 100)),
    INPUT_TYPE_SWITCH_DELAY("input_switch_delay_ceiling", () -> ThreadLocalRandom.current().nextInt(200)),
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
