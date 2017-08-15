package com.codelanx.aether.common.bot;

public class Invalidator {

    private final boolean all;
    private final int range;
    private final boolean end;

    Invalidator(boolean all, int range) {
        this(all, range, false);
    }
    
    Invalidator(boolean all, int range, boolean end) {
        this.all = all;
        this.range = range;
        this.end = end;
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
    
    public boolean isEnd() {
        return this.end;
    }

    public int getRange() {
        return this.range;
    }

    @Override
    public String toString() {
        return "Invalidator{" +
                "all=" + all +
                ", range=" + range +
                '}';
    }
}
