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

}
