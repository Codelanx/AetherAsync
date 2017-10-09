package com.codelanx.aether.common.input.type;

import com.codelanx.aether.common.input.InputTarget;

import java.util.function.Supplier;

/**
 * Created by rogue on 8/13/2017.
 */
public class RunemateTarget extends InputTarget {
    
    private final Supplier<Boolean> runemate;
    private final String debug;

    public RunemateTarget(Supplier<Boolean> runemate) {
        this(null, runemate);
    }

    public RunemateTarget(String debugDescription, Supplier<Boolean> runemate) {
        this.runemate = runemate;
        this.debug = debugDescription;
    }
    
    @Override
    public void attempt() {
        this.doAttempt(this.runemate);
    }

    @Override
    public String toString() {
        return this.debug == null
                ? super.toString().substring(this.getClass().getName().length() - this.getClass().getSimpleName().length())
                : "RunemateTarget{debug: " + this.debug + "}";
    }
}
