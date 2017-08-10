package com.codelanx.aether.cooking;

import com.codelanx.aether.common.AetherBot;
import com.codelanx.aether.common.branch.recipe.RecipeTask;
import com.codelanx.aether.common.mission.Mission;

public class CookingBot extends AetherBot {

    private static CookingBot instance; //yuck

    public CookingBot() {
        CookingBot.instance = this;
    }

    @Override
    public void onStart(String... strings) {
        super.onStart(strings);
        this.getBrain().register(Mission.of(new RecipeTask(BasicRecipe.R_LOBSTER)));
    }

    //let's try to avoid this
    public static CookingBot get() {
        return CookingBot.instance;
    }

}
