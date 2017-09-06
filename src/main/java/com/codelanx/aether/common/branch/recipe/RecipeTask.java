package com.codelanx.aether.common.branch.recipe;

import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.branch.bank.BankTask;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.codelanx.commons.logging.Logging;
import com.runemate.game.api.hybrid.Environment;

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
        return () -> {
            Logging.info("Checking recipe: " + this.recipe);
            return this.recipe.getRemainder() > 0;
        };
    }
}
