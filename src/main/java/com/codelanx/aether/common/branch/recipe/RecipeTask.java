package com.codelanx.aether.common.branch.recipe;

import com.codelanx.aether.common.Interactables;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.branch.GoToTargetTask;
import com.codelanx.aether.common.branch.bank.BankTask;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.codelanx.commons.logging.Logging;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GameObject;

import java.util.Optional;
import java.util.function.Supplier;

public class RecipeTask extends AetherTask<Boolean> {

    private final Recipe recipe;

    public RecipeTask(Recipe recipe) {
        this(recipe, null);
    }

    public RecipeTask(Recipe recipe, AetherTask<?> activateOverride) {
        this(recipe, activateOverride, activateOverride != null);
    }

    //useOverride is basically fluff to prevent constructor sig clash
    private RecipeTask(Recipe recipe, AetherTask<?> override, boolean useOverride) {
        this.recipe = recipe;
        AetherTask<?> incomplete;
        if (useOverride) {
            incomplete = override;
        } else {
            incomplete = new TargetSelectTask(recipe);
            Environment.getLogger().info("recipe: " + recipe);
            Environment.getLogger().info("type: " + Optional.ofNullable(recipe).map(Recipe::getRecipeType).map(Enum::name).orElse(null));
            switch (recipe.getRecipeType()) {
                case COOK:
                    incomplete = new GoToTargetTask<>(RecipeTask::findRange, new TargetSelectTask(recipe));
                    break;
                case SMELT:
                    incomplete = new GoToTargetTask<>(RecipeTask::findFurnace, new TargetSelectTask(recipe));
                    break;
            }
        }
        this.register(true, incomplete);
        this.register(false, new BankTask(recipe));
    }

    //TODO: private/remove
    public static GameObject findRange() {
        return Interactables.RANGE.queryGlobal().findAny().orElse(null);
    }

    private static GameObject findFurnace() {
        return Interactables.FURNACE.queryGlobal().findAny().orElse(null);
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> {
            Logging.info("Checking recipe: " + this.recipe);
            return this.recipe.getRemainder() > 0;
        };
    }
}
