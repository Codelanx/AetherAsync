package com.codelanx.aether.common.bot.neuron;

import com.codelanx.aether.common.bot.Brain;

//the "classic" form of antiban, will determinately select non-specific bot tasks (a not used example: skill hovering)
public class DeterminateInputNeuron extends Neuron {

    @Override
    public boolean applies() {
        return false;
    }

    @Override
    public void fire(Brain brain) {

    }
}
