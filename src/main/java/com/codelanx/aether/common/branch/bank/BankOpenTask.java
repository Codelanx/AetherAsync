package com.codelanx.aether.common.branch.bank;

import com.codelanx.aether.common.RunnableLeaf;
import com.codelanx.aether.common.recipe.Recipe;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.TreeTask;

public class BankOpenTask extends BranchTask {

    private final TreeTask success;
    private final TreeTask failure;

    public BankOpenTask(Recipe recipe) {
        this.success = new WithdrawTask(recipe);
        this.failure = RunnableLeaf.of(Bank::open);
    }

    @Override
    public TreeTask successTask() {
        return this.success;
    }

    @Override
    public boolean validate() {
        return Bank.isOpen();
    }

    @Override
    public TreeTask failureTask() {
        return this.failure;
    }
}
