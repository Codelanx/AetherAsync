package com.codelanx.aether.common;

import com.codelanx.commons.config.MemoryConfig;
import com.runemate.game.api.hybrid.player_sense.PlayerSense;

import java.util.function.Supplier;

public enum Randomization implements MemoryConfig<Number> {

    //EXAMPLE("some-key", () -> ThreadLocalRandom.current().nextDouble(0.25D, 0.5D)),
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

    @Override
    public Number getValue() {
        return (Number) PlayerSense.get(this.key); //could also #as, but we've specified Number generically
    }
}
