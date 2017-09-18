package com.codelanx.aether.common.branch.recipe;

import com.codelanx.aether.common.Interactables;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.branch.GoToTargetTask;
import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.json.item.ItemStack;
import com.codelanx.aether.common.json.item.Material;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.Players;

import java.util.Optional;
import java.util.function.Supplier;

public class RecipeExecTask extends AetherTask<Boolean> {

    private final Recipe recipe;
    private long lastInvalidation = System.currentTimeMillis();

    public RecipeExecTask(Recipe recipe) {
        this.recipe = recipe;
        AetherTask<?> failure = new TargetSelectTask(recipe);
        Environment.getLogger().info("recipe: " + recipe);
        Environment.getLogger().info("type: " + Optional.ofNullable(recipe).map(Recipe::getRecipeType).map(Enum::name).orElse(null));
        switch (recipe.getRecipeType()) {
            case COOK:
                failure = new GoToTargetTask<>(RecipeExecTask::findRange, new TargetSelectTask(recipe));
                break;
            case SMELT:
                failure = new GoToTargetTask<>(RecipeExecTask::findFurnace, new TargetSelectTask(recipe));
                break;
        }
        this.registerRunemateCall(true, () -> {
            this.recipe.getIngredients().map(ItemStack::getMaterial).map(Material::toInquiry).forEach(Caches.forInventory()::invalidateByType);
            if (this.lastInvalidation > System.currentTimeMillis() - 2000) {
                this.lastInvalidation = System.currentTimeMillis();
                return true;
            }
            return false;
        });
        this.register(false, failure);
    }

    @Override
    public Supplier<Boolean> getStateNow() { //TODO: abstract
        //896 == cooking
        return () -> Players.getLocal().getAnimationId() != -1;//896;
    }

    //TODO: private/remove
    public static GameObject findRange() {
        return Interactables.RANGE.queryGlobal().findAny().orElse(null);
    }

    private static GameObject findFurnace() {
        return Interactables.FURNACE.queryGlobal().findAny().orElse(null);
    }

}