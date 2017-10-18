package com.codelanx.aether.common.bot.neuron;

import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.Brain;
import com.codelanx.aether.common.input.UserInput;
import com.codelanx.commons.logging.Logging;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.script.framework.task.Task;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameEventNeuron extends Neuron {

    private static final boolean ENABLED = true; //false atm because there's a looping event bug
    private final AtomicBoolean handlingGameEvent = new AtomicBoolean();

    @Override
    public boolean applies() {
        return ENABLED && Aether.getBot().getGameEventController() != null && !this.handlingGameEvent.get();
    }

    @Override
    public void fire(Brain brain) {
        Task t = Aether.getBot().getGameEventController();
        this.handlingGameEvent.set(true);
        Logging.info("[Bot] Registering game event task...");
        Logging.info(t.getClass().getName());
        Logging.info(Objects.toString(t.getChildren()));
        Logging.info(Objects.toString(t.getParent()));
        UserInput.runemateInput(() -> {
            if (t.validate()) {
                t.execute();
            }
            this.handlingGameEvent.set(false);
            return true;
        });
    }

    @Override
    public boolean isBlocking() {
        return Environment.getBot().getGameEventController() != null;
    }

    @Override
    public boolean isEvaluationSkipped() {
        return this.handlingGameEvent.get();
    }
}
