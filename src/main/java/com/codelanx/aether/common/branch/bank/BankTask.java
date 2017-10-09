package com.codelanx.aether.common.branch.bank;

import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.branch.GoToTargetTask;
import com.codelanx.aether.common.branch.bank.withdraw.BankRecipeTask;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.Banks;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class BankTask extends AetherTask<Boolean> {

    public BankTask(Recipe recipe) {
        this.register(true, new BankRecipeTask(recipe));
        Supplier<? extends LocatableEntity> bank = () -> Stream.of(Banks.getLoadedBankBooths(), Banks.getLoadedBankChests(), Banks.getLoadedBankers())
                .map(LocatableEntityQueryResults::nearest).filter(Objects::nonNull).findFirst().orElse(null);
        this.register(false, new GoToTargetTask<>(bank, Bank::open));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return Bank::isOpen;
    }

}
