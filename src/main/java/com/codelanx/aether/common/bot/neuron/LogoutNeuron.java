package com.codelanx.aether.common.bot.neuron;

import com.codelanx.aether.common.bot.Brain;

public class LogoutNeuron extends Neuron {

    @Override
    public boolean applies() {
        return false;
    }

    @Override
    public void fire(Brain brain) {
        //TODO: logout handling, snipped for now (incomplete)
    }
}
