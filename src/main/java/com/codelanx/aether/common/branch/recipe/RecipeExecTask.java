package com.codelanx.aether.common.branch.recipe;

import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.branch.recipe.type.GoToFurnaceTask;
import com.codelanx.aether.common.branch.recipe.type.GoToRangeTask;
import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.json.item.ItemStack;
import com.codelanx.aether.common.json.item.Material;
import com.codelanx.aether.common.json.recipe.Recipe;
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
                failure = of(new CreateTask(recipe));
                break;
        }
        this.registerRunemateCall(true, () -> {
            this.recipe.getIngredients().map(ItemStack::getMaterial).map(Material::toInquiry).forEach(Caches.forInventory()::invalidateByType);
            return true;
        });
        this.register(false, failure);
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> Players.getLocal().getAnimationId() == 896;
    }

}