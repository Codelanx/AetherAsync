package com.codelanx.aether.common.mission;

import com.codelanx.aether.common.bot.mission.Mission;
import com.codelanx.aether.common.branch.recipe.RecipeTask;
import com.codelanx.aether.common.json.recipe.Recipe;

public class RecipeMission extends Mission<Boolean> {

    public RecipeMission(Recipe recipe) {
        super(new RecipeTask(recipe));
    }

    @Override
    public boolean hasEnded() {
        return false;
    }
}
