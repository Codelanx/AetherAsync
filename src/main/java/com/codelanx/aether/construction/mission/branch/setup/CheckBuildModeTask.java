package com.codelanx.aether.construction.mission.branch.setup;

import com.codelanx.aether.construction.BasicBitchBot;
import com.codelanx.aether.common.RunnableLeaf;
import com.runemate.game.api.hybrid.local.House;
import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.TreeTask;

public class CheckBuildModeTask extends BranchTask {

    private final TreeTask success;

    public CheckBuildModeTask(TreeTask success) {
        this.success = success;
    }

    @Override
    public boolean validate() {
        return House.Options.isBuildingModeOn();
    }

    @Override
    public TreeTask failureTask() {
        return RunnableLeaf.of(() -> House.Options.setBuildingMode(true));
    }

    @Override
    public TreeTask successTask() {
        BasicBitchBot.get().getBrain().getCurrentMission().setRoot(this.success);
        return this.success;
    }
}
