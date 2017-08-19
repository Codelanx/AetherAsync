package com.codelanx.aether.bots.defender.branch;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.bots.defender.leaf.ReEnterBasement;
import com.codelanx.aether.bots.defender.leaf.GoUpLadder;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;

import java.util.function.Supplier;

//import path.to.your.ReEnterBasement
//import path.to.your.GoUpLadder

/**
 * NOTES:
 * 
 */
public class BasementReadyToReEnter extends AetherTask<Boolean> {

    public BasementReadyToReEnter(DefenderBot bot) {
        this.register(true, new ReEnterBasement(bot));
        this.register(false, new GoUpLadder(bot));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> Inventory.getQuantity("Warrior guild token") > 150 && !Equipment.containsAnyOf("Rune defender") && !Inventory.containsAnyOf("Rune defender") && Inventory.newQuery().actions("Eat").results().first() != null;
    }
}
