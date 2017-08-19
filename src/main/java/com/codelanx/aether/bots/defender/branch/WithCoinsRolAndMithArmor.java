package com.codelanx.aether.bots.defender.branch;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.bots.defender.leaf.UnPrepared;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;

import java.util.function.Supplier;

//import path.to.your.WithFood
//import path.to.your.UnPrepared

/**
 * NOTES:
 * Makes sure we have coins in inv, rol equipped, and mith armor pieces in inv
 */
public class WithCoinsRolAndMithArmor extends AetherTask<Boolean> {

    public WithCoinsRolAndMithArmor(DefenderBot bot) {
        this.register(true, new WithFood(bot));
        this.register(false, new UnPrepared(bot));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> Inventory.containsAllOf(/*"Coins", */"Black full helm", "Black platebody", "Black platelegs");// && Equipment.contains("Ring of life");
    }

}
