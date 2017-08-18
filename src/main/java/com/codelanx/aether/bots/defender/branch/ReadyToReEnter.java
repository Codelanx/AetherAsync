package com.codelanx.aether.bots.defender.branch;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.bots.defender.leaf.LeaveTopFloor;
import com.codelanx.aether.bots.defender.leaf.ReEnter;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;

import java.util.function.Supplier;

//import path.to.your.ReEnter
//import path.to.your.LeaveTopFloor

/**
 * NOTES:
 * check if we are still working towards a rune defender, if we have food, and if we have enough tokens.  If these are all true, we want to re-enter.  If any are false, return false
 */
public class ReadyToReEnter extends AetherTask<Boolean> {

    public ReadyToReEnter(DefenderBot bot) {
        this.register(true, new ReEnter(bot));
        this.register(false, new LeaveTopFloor(bot));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> {
            return !Inventory.containsAnyOf("Rune defender", "Dragon defender")
                    && !Equipment.containsAnyOf("Rune defender", "Dragon defender")
                    && Inventory.getQuantity("Warrior guild token") > 150
                    && Inventory.newQuery().actions("Eat").results().first() != null;
        };
    }
}
