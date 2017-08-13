package com.codelanx.aether.common.branch.async.recipe;

import com.codelanx.aether.common.bot.async.task.AetherTask;
import com.codelanx.aether.common.branch.async.bank.BankTask;
import com.codelanx.aether.common.recipe.Recipe;

import java.util.function.Supplier;

public class RecipeTask extends AetherTask<Boolean> {

    private final Recipe recipe;

    public RecipeTask(Recipe recipe) {
        this.recipe = recipe;
        this.register(true, new RecipeExecTask(recipe));
        this.register(false, new BankTask(recipe));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> this.recipe.getRemainder() > 0;
    }
}
