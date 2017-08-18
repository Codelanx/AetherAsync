package com.codelanx.aether.bots.defender.branch;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.region.Players;

import java.util.function.Supplier;

//import path.to.your.DetermineUpOrDown
//import path.to.your.OnBasement

/**
 * NOTES:
 * 
 */
public class OnMiddleFloor extends AetherTask<Boolean> {

    public OnMiddleFloor(DefenderBot bot) {
        this.register(true, new DetermineUpOrDown(bot));
        this.register(false, new OnBasement(bot));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> Players.getLocal() != null && Players.getLocal().getPosition().getPlane() == 1;
    }
}
