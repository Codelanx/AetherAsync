package com.codelanx.aether.common.branch.bank;

import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.branch.GoToTargetTask;
import com.codelanx.aether.common.branch.bank.withdraw.BankRecipeTask;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.region.Banks;

import java.util.function.Supplier;

public class BankTask extends AetherTask<Boolean> {

    public BankTask(Recipe recipe) {
        this.register(true, new BankRecipeTask(recipe));
        this.register(false, new GoToTargetTask<>(Banks.getLoadedBankBooths()::nearest, Bank::open));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return Bank::isOpen;
    }

}
