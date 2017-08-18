package com.codelanx.aether.bots.defender.branch;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.Shop;

import java.util.function.Supplier;

/**
 * NOTES:
 * 
 */
public class WithFood extends AetherTask<Boolean> {

    private DefenderBot bot;

    public WithFood(DefenderBot bot) {
        this.register(true, new WithTokens(bot));
        this.register(false, new ShopInterfaceOpen(bot));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> !Shop.isOpen() && Inventory.newQuery().actions("Eat").results().size() > 0;
    }
}
