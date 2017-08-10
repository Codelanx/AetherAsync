package com.codelanx.aether.crafting;

import com.codelanx.aether.common.AetherBot;
import com.codelanx.aether.common.branch.recipe.RecipeTask;
import com.codelanx.aether.common.mission.Mission;

public class CraftingBot extends AetherBot {

    private static CraftingBot instance; //yuck

    public CraftingBot() {
        CraftingBot.instance = this;
    }

    @Override
    public void onStart(String... strings) {
        super.onStart(strings);
        this.getBrain().register(Mission.of(new RecipeTask(CraftingRecipe.GOLD_BRACLET)));
    }

    //let's try to avoid this
    public static CraftingBot get() {
        return CraftingBot.instance;
    }

}