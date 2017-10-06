package com.codelanx.aether.common.bot;

import com.codelanx.aether.common.bot.neuron.BotFailureNeuron;
import com.codelanx.aether.common.bot.neuron.CombatNeuron;
import com.codelanx.aether.common.bot.neuron.GameEventNeuron;
import com.codelanx.aether.common.bot.neuron.LogoutNeuron;
import com.codelanx.aether.common.bot.neuron.LootNeuron;
import com.codelanx.aether.common.bot.neuron.Neuron;
import com.codelanx.aether.common.bot.neuron.LogicTreeNeuron;
import com.codelanx.aether.common.bot.neuron.UserInputNeuron;
import com.codelanx.commons.logging.Logging;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Brain {

    private final LogicTreeNeuron primary = new LogicTreeNeuron();
    private final List<? extends Neuron> neurons = Collections.unmodifiableList(Arrays.asList(
            new UserInputNeuron(),
            new GameEventNeuron(),
            new CombatNeuron(),
            new BotFailureNeuron(),
            new LootNeuron(),
            this.primary,
            new LogoutNeuron()
    ));
    private final AsyncBot bot;

    public Brain(AsyncBot bot) {
        this.bot = bot;
    }

    public AsyncBot getBot() {
        return this.bot;
    }

    public LogicTreeNeuron getLogicTree() {
        return this.primary;
    }

    void loop() {
        //Neuron n = this.neurons.stream().filter(Neuron::applies).findFirst().orElse(null);
        Neuron n = null;
        for (int i = 0; i < this.neurons.size(); i++) {
            Neuron nn = this.neurons.get(i);
            boolean blocked = nn.isBlocking();
            boolean skipped = nn.isEvaluationSkipped();
            if (skipped) {
                if (blocked) {
                    return;
                }
            } else if (nn.applies()) {
                n = nn;
                break;
            }
        }
        if (n == null) {
            //stop bot
            Logging.severe("No neurons available to be fired, bot finished");
            this.bot.stop();
            return;
        }
        Logging.info("Firing neuron: " + n.getClass().getSimpleName());
        n.fire(this);
    }

}
