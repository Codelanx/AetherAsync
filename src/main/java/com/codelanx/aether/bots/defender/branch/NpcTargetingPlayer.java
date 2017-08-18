package com.codelanx.aether.bots.defender.branch;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.bots.defender.leaf.AttackNpcTargetingUs;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;

import java.util.function.Supplier;

//import path.to.your.AttackNpcTargetingUs
//import path.to.your.InAnimationRoom

/**
 * NOTES:
 * 
 */
public class NpcTargetingPlayer extends AetherTask<Boolean> {

    public NpcTargetingPlayer(DefenderBot bot) {
        this.register(true, new AttackNpcTargetingUs(bot));
        this.register(false, new InAnimationRoom(bot));
    }
    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> Npcs.newQuery().targeting(Players.getLocal()).reachable().results().nearest() != null;
    }

}
