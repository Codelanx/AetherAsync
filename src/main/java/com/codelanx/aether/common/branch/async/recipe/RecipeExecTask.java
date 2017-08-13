package com.codelanx.aether.common.branch.async.recipe;

import com.codelanx.aether.common.bot.sync.AetherBot;
import com.codelanx.aether.common.bot.async.task.AetherTask;
import com.codelanx.aether.common.branch.async.recipe.type.GoToFurnaceTask;
import com.codelanx.aether.common.branch.async.recipe.type.GoToRangeTask;
import com.codelanx.aether.common.item.ItemStack;
import com.codelanx.aether.common.recipe.Recipe;
import com.runemate.game.api.hybrid.region.Players;

import java.util.function.Supplier;

public class RecipeExecTask extends AetherTask<Boolean> {

    private final Recipe recipe;

    public RecipeExecTask(Recipe recipe) {
        this.recipe = recipe;
        AetherTask<?> failure;
        switch (recipe.getRecipeType()) {
            case COOK:
                failure = new GoToRangeTask(recipe);
                break;
            case SMELT:
                failure = new GoToFurnaceTask(recipe);
                break;
            default:
                failure = AetherTask.of(new CreateTask(recipe));
                break;
        }
        this.registerRunemateCall(true, () -> {
            this.recipe.getIngredients().map(ItemStack::getMaterial).forEach(AetherBot.get().getInventory()::invalidate);
            return true;
        });
        this.register(false, failure);
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> Players.getLocal().getAnimationId() == 896;
    }

}