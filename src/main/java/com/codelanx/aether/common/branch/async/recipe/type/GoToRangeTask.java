package com.codelanx.aether.common.branch.async.recipe.type;

import com.codelanx.aether.common.Interactables;
import com.codelanx.aether.common.branch.async.CommonTasks;
import com.codelanx.aether.common.recipe.Recipe;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;

public class GoToRangeTask extends GoToTargetTask<GameObject> {

    public GoToRangeTask(Recipe recipe) {
        super(GoToRangeTask::findRange, recipe);
        this.registerInvalidator(null, CommonTasks.MOVE_TO_RANGE);
    }

    public static GameObject findRange() {
        LocatableEntityQueryResults<GameObject> res = Interactables.RANGE.query();
        return res.isEmpty() ? null : res.nearest();
    }

}
