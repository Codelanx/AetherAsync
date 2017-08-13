package com.codelanx.aether.common.bot.async;

public class Invalidators {

    public static final Invalidator ALL = new Invalidator(true, -1);
    public static final Invalidator NONE = new Invalidator(false, -1);

    /**
     * Invalidates {@code range} previous branches, where {@code range}
     * is a positive non-zero integer
     *
     * @param range A positive, non-zero integer describing the number
     *              of previous branches to invalidate
     */
    public static Invalidator range(int range) {
        return new Invalidator(false, range);
    }

}
