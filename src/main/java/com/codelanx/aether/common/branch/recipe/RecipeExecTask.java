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
            return true;
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
        LocatableEntityQueryResults<GameObject> res = Interactables.RANGE.query();
        return res.isEmpty() ? null : res.nearest();
    }

    private static GameObject findFurnace() {
        LocatableEntityQueryResults<GameObject> res = Interactables.FURNACE.query();
        return res.isEmpty() ? null : res.nearest();
    }

}