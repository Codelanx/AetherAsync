package com.codelanx.aether.bots.defender.branch;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.region.Players;

import java.util.function.Supplier;


/**
 * NOTES:
 * 
 */
public class OnTopFloor extends AetherTask<Boolean> {

    public OnTopFloor(DefenderBot bot) {
        this.register(true, new ReadyToReEnter(bot));
        this.register(false, new OnMiddleFloor(bot));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> Players.getLocal() != null && Players.getLocal().getPosition().getPlane() == 2;
    }
}
