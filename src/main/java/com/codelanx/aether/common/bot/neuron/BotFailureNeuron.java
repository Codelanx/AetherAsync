package com.codelanx.aether.common.bot.neuron;

import com.codelanx.aether.common.bot.Brain;

//responsible for detecting failure loops/failed interactions/bad pathing/exceptions/etc.
//if fired, will attempt to move the bot to a safe place before logging out and
//explaining to the user how/why the bot failed
public class BotFailureNeuron extends Neuron {
    @Override
    public boolean applies() {
        return false;
    }

    @Override
    public void fire(Brain brain) {

    }
}
