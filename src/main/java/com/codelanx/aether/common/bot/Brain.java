package com.codelanx.aether.common.bot;

import com.codelanx.aether.common.bot.neuron.CombatNeuron;
import com.codelanx.aether.common.bot.neuron.GameEventNeuron;
import com.codelanx.aether.common.bot.neuron.Neuron;
import com.codelanx.aether.common.bot.neuron.PrimaryNeuron;
import com.codelanx.aether.common.bot.neuron.UserInputNeuron;
import com.codelanx.commons.logging.Logging;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Brain {

    private final PrimaryNeuron logicTree = new PrimaryNeuron();
    private final List<? extends Neuron> neurons = Collections.unmodifiableList(Arrays.asList(
            new UserInputNeuron(),
            new GameEventNeuron(),
            new CombatNeuron(),
            this.logicTree
    ));
    private final AsyncBot bot;

    public Brain(AsyncBot bot) {
        this.bot = bot;
    }

    public AsyncBot getBot() {
        return this.bot;
    }

    public PrimaryNeuron getLogicTree() {
        return this.logicTree;
    }

    void loop() {
        Neuron n = this.neurons.stream().filter(Neuron::applies).findFirst().orElse(null);
        if (n != null) {
            n.fire(this);
            return;
        }
        //stop bot
        Logging.severe("No neurons available to be fired, bot finished");
        this.bot.stop();
    }

}
