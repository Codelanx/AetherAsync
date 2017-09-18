package com.codelanx.aether.common.bot.neuron;

import com.codelanx.aether.common.bot.Brain;

//represents brain tasks which feed in information
//these are separate from the logic tree and are mostly for higher-level
//concepts such as antiban, combat management, and chatting
//
//They are considered independent logic trees which are determinately executed
public abstract class Neuron {

    //this is a -synchronous- method, in that it will block the main logic thread
    //care should be taken to use caching or other means in order to have this
    //method return as fast as possible
    public abstract boolean applies();

    //fires this neuron, aka executing whatever neuron tasks need be executed
    public abstract void fire(Brain brain);

    //returns true when this neuron is busy calculating state changes (e.g. input)
    //this is to allow resting the bot and not validating state that will soon be potentially invalid
    public boolean isBlocking() {
        return false;
    }

    //returns whether or not this neuron is skipped
    //if a neuron is both skipped and blocking, it acts as a sleep on the main bot thread
    public boolean isEvaluationSkipped() {
        return false;
    }

}
