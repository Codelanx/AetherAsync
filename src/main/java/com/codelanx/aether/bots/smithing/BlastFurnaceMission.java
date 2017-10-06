package com.codelanx.aether.bots.smithing;

import com.codelanx.aether.bots.smithing.branch.BlastFurnaceTask;
import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.mission.Mission;
import com.codelanx.aether.common.json.item.Material;
import com.codelanx.aether.common.json.recipe.Recipe;

public class BlastFurnaceMission extends Mission<BlastFurnaceTask.BlastState> {

    public BlastFurnaceMission(Recipe bar) {
        super(new BlastFurnaceTask(modifyBar(bar)));
    }

    private static Recipe modifyBar(Recipe bar) {
        Material coal = Aether.getBot().getData().getItem("Coal");
        bar = bar.modify(item -> {
            if (!coal.equals(item.getMaterial())) {
                return item;
            }
            return item.setQuantity(item.getQuantity() >> 1); //halve coal for blast furnace
        });
        return bar;
    }

    @Override
    public boolean hasEnded() {
        return false;
    }


}
