package com.codelanx.aether.common.bot.neuron;

import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.Brain;
import com.codelanx.commons.logging.Logging;
import com.runemate.game.api.script.framework.task.Task;

import java.util.concurrent.atomic.AtomicBoolean;

public class GameEventNeuron extends Neuron {

    private final AtomicBoolean handlingGameEvent = new AtomicBoolean();

    @Override
    public boolean applies() {
        return Aether.getBot().getGameEventController() != null && !this.handlingGameEvent.get();
    }

    @Override
    public void fire(Brain brain) {
        Task t = Aether.getBot().getGameEventController();
        this.handlingGameEvent.set(true);
        Logging.info("[Bot] Registering game event task...");
        Logging.info(t.getClass().getName());
        Logging.info(t.getChildren().toString());
        Logging.info(t.getParent().toString());
        brain.getLogicTree().registerImmediate(() -> {
            if (t.validate()) {
                brain.getLogicTree().registerImmediate(() -> {
                    t.execute();
                    this.handlingGameEvent.set(false);
                });
            }
        });
    }
}
