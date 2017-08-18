package com.codelanx.aether.bots.defender.branch;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.bots.defender.leaf.Stop;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;

import java.util.function.Supplier;

//import path.to.your.Stop
//import path.to.your.BasementReadyToReEnter

/**
 * NOTES:
 * 
 */
public class WithDragonDefender extends AetherTask<Boolean> {

    public WithDragonDefender(DefenderBot bot) {
        this.register(true, new Stop(bot));
        this.register(false, new BasementReadyToReEnter(bot));
    }

    private Stop stop;
    private BasementReadyToReEnter basementreadytoreenter;

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> Inventory.contains("Dragon defender") || Equipment.contains("Dragon defender");
    }

}
