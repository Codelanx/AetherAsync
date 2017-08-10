package com.codelanx.aether.common.branch.recipe;

import com.codelanx.aether.common.AetherBot;
import com.codelanx.aether.common.CommonActions;
import com.codelanx.aether.common.branch.recipe.type.GoToFurnaceTask;
import com.codelanx.aether.common.branch.recipe.type.GoToRangeTask;
import com.codelanx.aether.common.item.ItemStack;
import com.codelanx.aether.common.recipe.Recipe;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.TreeTask;

public class RecipeExecTask extends BranchTask {

    private final TreeTask success;
    private final Recipe recipe;

    public RecipeExecTask(Recipe recipe) {
        this.recipe = recipe;
        TreeTask success;
        switch (recipe.getRecipeType()) {
            case COOK:
                success = new GoToRangeTask(recipe);
                break;
            case SMELT:
                success = new GoToFurnaceTask(recipe);
                break;
            default:
                success = new CreateTask(recipe);
                break;
        }
        this.success = success;
    }

    @Override
    public TreeTask successTask() {
        this.recipe.getIngredients().map(ItemStack::getMaterial).forEach(AetherBot.get().getInventory()::invalidate);
        return CommonActions.WAIT.getTask();
    }

    @Override
    public boolean validate() {
        return Players.getLocal().getAnimationId() == 896;
    }

    @Override
    public TreeTask failureTask() {
        return this.success;
    }
}