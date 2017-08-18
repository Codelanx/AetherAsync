package com.codelanx.aether.bots.defender.branch;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.bots.defender.leaf.Eat;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.region.Players;

import java.util.function.Supplier;

//import path.to.your.InCombat
//import path.to.your.Eat

/**
 * NOTES:
 * 
 */
public class SufficientHealth extends AetherTask<Boolean> {

    private DefenderBot bot;

    public SufficientHealth(DefenderBot bot) {
        this.bot = bot;
        this.register(false, new Eat(bot));
        this.register(true, new InCombat(bot));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> Players.getLocal() != null && Health.getCurrentPercent() >= 55;
    }
}
