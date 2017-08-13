package com.codelanx.aether.common.branch.sync.bank;

import com.codelanx.aether.common.recipe.Recipe;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.location.navigation.Landmark;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.region.Banks;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.hybrid.util.calculations.Distance.Algorithm;
import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.LeafTask;
import com.runemate.game.api.script.framework.tree.TreeTask;

//walks to bank, and withdraws
public class BankTask extends BranchTask {

    private final TreeTask success;
    private final TreeTask failure;

    public BankTask(Recipe recipe) {
        this.success = new BankOpenTask(recipe);
        this.failure = new LeafTask() {
            @Override
            public void execute() {
                //TODO: Cache
                WebPath path = Traversal.getDefaultWeb().getPathBuilder().buildTo(Landmark.BANK);
                path.step();
            }
        };
    }

    @Override
    public TreeTask successTask() {
        return this.success;
    }

    @Override
    public boolean validate() {
        GameObject booth = Banks.getLoadedBankBooths().nearest();
        return booth != null && booth.isVisible() && Distance.between(booth, Players.getLocal(), Algorithm.EUCLIDEAN_SQUARED) < 16;
    }

    @Override
    public TreeTask failureTask() {
        return this.failure;
    }
}
