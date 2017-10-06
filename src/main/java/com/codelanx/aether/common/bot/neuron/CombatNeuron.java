package com.codelanx.aether.common.bot.neuron;

import com.codelanx.aether.common.bot.Brain;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.region.Players;

public class CombatNeuron extends Neuron {

    @Override
    public boolean applies() {
        if (true) {
            return false;
        }
        Player p = Players.getLocal();
        return p.getTarget() != null || p.getHealthGauge() != null;
    }

    @Override
    public void fire(Brain brain) {
        //TODO: Combat management, snipped for now (incomplete)
    }

    @Override
    public boolean isEvaluationSkipped() {
        return true;
    }

    @Override
    public boolean isBlocking() {
        return false;
    }
}
