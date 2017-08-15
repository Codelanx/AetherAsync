package com.codelanx.aether.common.branch.recipe.type;

import com.codelanx.aether.common.Interactables;
import com.codelanx.aether.common.branch.CommonTasks;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;

public class GoToFurnaceTask extends GoToTargetTask<GameObject> {

    public GoToFurnaceTask(Recipe recipe) {
        super(GoToFurnaceTask::findFurnace, recipe);
        this.registerInvalidator(null, CommonTasks.MOVE_TO_FURNACE);
    }

    public static GameObject findFurnace() {
        LocatableEntityQueryResults<GameObject> res = Interactables.FURNACE.query();
        
        return res.isEmpty() ? null : res.nearest();
    }
}
