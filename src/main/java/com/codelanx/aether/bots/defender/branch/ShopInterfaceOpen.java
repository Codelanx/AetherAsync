package com.codelanx.aether.bots.defender.branch;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Shop;

import java.util.function.Supplier;

/**
 * NOTES:
 * 
 */
public class ShopInterfaceOpen extends AetherTask<Boolean> {

    public ShopInterfaceOpen(DefenderBot bot) {
        this.register(true, new FoodInStock(bot));
        this.register(false, new InShop(bot));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> {
            boolean back = Shop.isOpen();
            Environment.getBot().getLogger().info("Shop interface" + (back ? "" : " not") + " detected");
            return back;
        };
    }

}
