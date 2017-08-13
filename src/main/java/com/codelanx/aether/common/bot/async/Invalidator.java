package com.codelanx.aether.common.bot.async;

public class Invalidator {

    private final boolean all;
    private final int range;


    Invalidator(boolean all, int range) {
        this.all = all;
        this.range = range;
    }

    public boolean isNone() {
        return !this.all && this.range <= 0;
    }

    public boolean isRange() {
        return this.range > 0;
    }

    public boolean isAll() {
        return this.all;
    }

    public int getRange() {
        return this.range;
    }

}
