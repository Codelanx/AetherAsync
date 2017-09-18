package com.codelanx.aether.bots.smithing;

import com.codelanx.aether.bots.smithing.branch.BlastFurnaceTask;
import com.codelanx.aether.common.bot.mission.Mission;
import com.codelanx.aether.common.json.recipe.Recipe;

public class BlastFurnaceMission extends Mission<BlastFurnaceTask.BlastState> {

    public BlastFurnaceMission(Recipe bar) {
        super(new BlastFurnaceTask(bar));
    }

    @Override
    public boolean hasEnded() {
        return false;
    }


}
