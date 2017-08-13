package com.codelanx.aether.common.bot.async.mouse;

/**
 * Created by rogue on 8/13/2017.
 */
public abstract class InputTarget {
    
    public abstract void attempt();

    public abstract boolean isAttempting();
    public abstract boolean isAttempted();
    public abstract boolean isSuccessful();
}
