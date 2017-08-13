package com.codelanx.aether.common.branch.sync.recipe;

import com.codelanx.aether.common.branch.sync.bank.BankTask;
import com.codelanx.aether.common.recipe.Recipe;
import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.TreeTask;

public class RecipeTask extends BranchTask {

    private final TreeTask success;
    private final TreeTask failure;
    private final Recipe recipe;

    public RecipeTask(Recipe recipe) {
        this.recipe = recipe;
        this.success = new RecipeExecTask(recipe);
        this.failure = new BankTask(recipe);
    }

    @Override
    public boolean validate() {
        return this.recipe.getRemainder() > 0;
    }

    @Override
    public TreeTask successTask() {
        return this.success;
    }

    @Override
    public TreeTask failureTask() {
        return this.failure;
    }
}
