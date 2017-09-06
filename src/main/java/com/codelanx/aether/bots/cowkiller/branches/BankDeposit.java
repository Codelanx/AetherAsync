package com.codelanx.aether.bots.cowkiller.branches;

import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;

import java.util.function.Supplier;

public class BankDeposit extends AetherTask<Boolean> {

    public BankDeposit() {
        this.registerRunemateCall(true, Bank::depositInventory);
        this.register(false, Bank::open);
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return Bank::isOpen;
    }

}