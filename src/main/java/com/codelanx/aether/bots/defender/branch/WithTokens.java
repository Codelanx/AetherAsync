package com.codelanx.aether.bots.defender.branch;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.bots.defender.leaf.GoToAnimationRoom;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;

import java.util.function.Supplier;

//import path.to.your.WithRuneDefender
//import path.to.your.GoToAnimationRoom

/**
 * NOTES:
 * 
 */
public class WithTokens extends AetherTask<Boolean> {

    public WithTokens(DefenderBot bot) {
        this.register(true, new WithRuneDefender(bot));
        this.register(false, new GoToAnimationRoom(bot));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> {
            boolean back = Inventory.getQuantity("Warrior guild token") >= 2400;
            if (!back) {
                Environment.getBot().getLogger().info("We don't have 2400 tokens");
            }
            return back;
        };
    }
}
