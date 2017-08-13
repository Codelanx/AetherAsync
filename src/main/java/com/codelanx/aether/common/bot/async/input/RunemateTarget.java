package com.codelanx.aether.common.bot.async.input;

import java.util.function.Supplier;

/**
 * Created by rogue on 8/13/2017.
 */
public class RunemateTarget extends InputTarget {
    
    private final Supplier<Boolean> runemate;
    
    public RunemateTarget(Supplier<Boolean> runemate) {
        this.runemate = runemate;
    }
    
    @Override
    public void attempt() {
        this.doAttempt(this.runemate);
    }
}
