package com.codelanx.aether.bots.defender.branch;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.bots.defender.leaf.GoDown;
import com.codelanx.aether.bots.defender.leaf.GoUp;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;

import java.util.function.Supplier;

//import path.to.your.GoUp
//import path.to.your.GoDown

/**
 * NOTES:
 * Decides if we should go up or down staircase.  If we have a rune defender, are out of food, or are out of tokens, we want to go down.  If we are still working towards a rune defender and we have enough food and tokens, we want to go up.
 */
public class DetermineUpOrDown extends AetherTask<Boolean> {

    public DetermineUpOrDown(DefenderBot bot) {
        this.register(true, new GoUp(bot));
        this.register(false, new GoDown(bot));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> (!(Inventory.contains("Rune defender") || Equipment.contains("Rune defender"))) && Inventory.newQuery().actions("Eat").results().size() > 0 && Inventory.getQuantity("Warrior guild token") > 150;
    }

}
