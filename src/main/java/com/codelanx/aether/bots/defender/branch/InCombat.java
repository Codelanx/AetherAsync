package com.codelanx.aether.bots.defender.branch;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.bots.defender.leaf.Wait;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.region.Players;

import java.util.function.Supplier;

//import path.to.your.Wait
//import path.to.your.NpcTargetingPlayer

/**
 * NOTES:
 * 
 */
public class InCombat extends AetherTask<Boolean> {

    public InCombat(DefenderBot bot) {
        this.register(true, new Wait(bot));
        this.register(false, new NpcTargetingPlayer(bot));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> Players.getLocal() != null && Players.getLocal().getTarget() != null;
    }
}
