package com.codelanx.aether.fletching;

import com.codelanx.aether.common.AetherBot;
import com.codelanx.aether.common.branch.recipe.RecipeTask;
import com.codelanx.aether.common.mission.Mission;

public class FletchingBot extends AetherBot {

    private static FletchingBot instance; //yuck

    public FletchingBot() {
        FletchingBot.instance = this;
    }

    @Override
    public void onStart(String... strings) {
        super.onStart(strings);
        this.getBrain().register(Mission.of(new RecipeTask(FletchingRecipe.STEEL_ARROWS)));
    }

    //let's try to avoid this
    public static FletchingBot get() {
        return FletchingBot.instance;
    }

}
