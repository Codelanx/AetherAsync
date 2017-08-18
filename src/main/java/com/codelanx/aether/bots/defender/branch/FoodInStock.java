package com.codelanx.aether.bots.defender.branch;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.bots.defender.leaf.BuyFood;
import com.codelanx.aether.bots.defender.leaf.WaitForNow;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.local.hud.interfaces.Shop;

import java.util.function.Supplier;

/**
 * NOTES:
 * 
 */
public class FoodInStock extends AetherTask<Boolean> {

    public FoodInStock(DefenderBot bot) {
        this.register(true, new BuyFood(bot));
        this.register(false, new WaitForNow(bot));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> Shop.isOpen() && (Shop.getQuantity("Potato with cheese") > 1 || Shop.getQuantity("Bass") > 1 || Shop.getQuantity("Plain pizza") > 1);
    }

}
