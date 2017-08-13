package com.codelanx.aether.common.branch.async.bank;

import com.codelanx.aether.common.bot.async.task.AetherTask;
import com.codelanx.aether.common.recipe.Recipe;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;

import java.util.function.Supplier;

public class BankOpenTask extends AetherTask<Boolean> {

    public BankOpenTask(Recipe recipe) {
        this.register(true, new WithdrawTask(recipe));
        this.registerRunemateCall(false, Bank::open);
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return Bank::isOpen;
    }

}
