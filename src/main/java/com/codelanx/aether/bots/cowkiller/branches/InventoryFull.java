package com.codelanx.aether.bots.cowkiller.branches;

import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;

import java.util.function.Supplier;

public class InventoryFull extends AetherTask<Boolean> {

    public InventoryFull() {
        this.register(true, new ContainsCowhide());
        this.register(false, new BankOpen());
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        //inventory full?
        return () -> Inventory.getEmptySlots() < 1;
    }

}