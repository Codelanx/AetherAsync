package com.codelanx.aether.bots.defender.branch;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.bots.defender.leaf.GoToShop;
import com.codelanx.aether.bots.defender.leaf.TradeNpc;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.Players;

import java.util.function.Supplier;

//import path.to.your.TradeNpc
//import path.to.your.GoToShop

/**
 * NOTES:
 * 
 */
public class InShop extends AetherTask<Boolean> {

    private static final Area shop = new Area.Rectangular(new Coordinate(2838, 3548, 0), new Coordinate(2843, 3555, 0));

    public InShop(DefenderBot bot) {
        this.register(true, new TradeNpc(bot));
        this.register(false, new GoToShop(bot));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> {
            boolean back = Players.getLocal() != null && shop.contains(Players.getLocal());
            Environment.getBot().getLogger().info("We are" + (back ? "" : " not") + " in the shop");
            return back;
        };
    }
}
