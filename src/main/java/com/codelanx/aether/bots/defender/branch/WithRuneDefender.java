package com.codelanx.aether.bots.defender.branch;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.bots.defender.leaf.GoToBasement;
import com.codelanx.aether.bots.defender.leaf.GoToMiddleFloor;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;

import java.util.function.Supplier;

//import path.to.your.GoToBasement
//import path.to.your.GoToMiddleFloor

/**
 * NOTES:
 * 
 */
public class WithRuneDefender extends AetherTask<Boolean> {

    public WithRuneDefender(DefenderBot bot) {
        this.register(true, new GoToBasement(bot));
        this.register(false, new GoToMiddleFloor(bot));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> Inventory.contains("Rune defender") || Equipment.contains("Rune defender");
    }

}
