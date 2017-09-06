package com.codelanx.aether.common.bot.neuron;

import com.codelanx.aether.common.bot.Brain;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.region.Players;

public class CombatNeuron extends Neuron {

    @Override
    public boolean applies() {
        Player p = Players.getLocal();
        return p.getTarget() != null || p.getHealthGauge() != null;
    }

    @Override
    public void fire(Brain brain) {
        //TODO: Combat management
        //determine health safety first
        if (false) { //health is low
            //eat food
        }
        //potion management
        if (false) {

        }
    }
}
