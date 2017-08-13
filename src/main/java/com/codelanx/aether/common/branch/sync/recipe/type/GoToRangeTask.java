package com.codelanx.aether.common.branch.sync.recipe.type;

import com.codelanx.aether.common.branch.sync.CommonActions;
import com.codelanx.aether.common.Interactables;
import com.codelanx.aether.common.branch.sync.recipe.TargetSelectTask;
import com.codelanx.aether.common.recipe.Recipe;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.hybrid.util.calculations.Distance.Algorithm;
import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.TreeTask;

public class GoToRangeTask extends BranchTask {

    private final TreeTask success;
    private long lastClickMS = 0;

    public GoToRangeTask(Recipe recipe) {
        this.success = new TargetSelectTask(recipe);
    }

    @Override
    public TreeTask successTask() {
        return this.success;
    }

    @Override
    public boolean validate() {
        GameObject obj = GoToRangeTask.findRange();
        if (obj != null) {
            double dist = Distance.between(obj, Players.getLocal(), Algorithm.EUCLIDEAN_SQUARED);
            if (dist < 5) {
                return true;
            }
        }
        return false;
    }

    public static GameObject findRange() {
        LocatableEntityQueryResults<GameObject> res = Interactables.RANGE.query();
        return res.isEmpty() ? null : res.nearest();
    }

    @Override
    public TreeTask failureTask() {
        return CommonActions.MOVE_TO_RANGE.getTask();
    }
}