package com.codelanx.aether.bots.defender.branch;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.bots.defender.leaf.DefenderOnGround;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.region.GroundItems;

import java.util.function.Supplier;

//import path.to.your.DefenderOnGround
//import path.to.your.NeedToLeaveRoom

/**
 * NOTES:
 * 
 */
public class DesiredItemOnGround extends AetherTask<Boolean> {

    private static final String desired[] = {"Mithril full helm", "Warrior guild token", "Mithril platebody", "Mithril platelegs", "Bronze defender", "Iron defender", "Steel defender", "Black defender", "Mithril defender", "Adamant defender", "Rune defender", "Dragon defender"};


    public DesiredItemOnGround(DefenderBot bot) {
        this.register(true, new DefenderOnGround(bot));
        this.register(false, new NeedToLeaveRoom(bot));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> GroundItems.newQuery().names(desired).reachable().results().first() != null;
    }

}
