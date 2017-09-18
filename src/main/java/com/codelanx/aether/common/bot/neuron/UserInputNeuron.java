package com.codelanx.aether.common.bot.neuron;

import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.Brain;
import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.input.UserInput;
import com.codelanx.aether.common.input.UserInputException;
import com.codelanx.commons.logging.Logging;

public class UserInputNeuron extends Neuron {

    @Override
    public boolean applies() {
        return UserInput.hasTasks();
    }

    @Override
    public void fire(Brain brain) {
        try {
            UserInput.attempt();
        } catch (UserInputException ex) {
            Logging.info("Error while attempting user input, invalidating bot and retrying...");
            Aether.getBot().getBrain().getLogicTree().invalidate();
            UserInput.wipe();
            Caches.invalidateAll();
        }
    }

    @Override
    public boolean isBlocking() {
        return UserInput.hasTasks();
    }
}
