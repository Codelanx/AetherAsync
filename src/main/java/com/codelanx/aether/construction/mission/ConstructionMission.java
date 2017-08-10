package com.codelanx.aether.construction.mission;

import com.codelanx.aether.common.mission.ConfigurableTask;
import com.codelanx.aether.common.mission.Mission;
import com.codelanx.aether.construction.mission.branch.build.BuildTask;
import com.codelanx.aether.construction.mission.branch.setup.GetToHouseTask;
import com.runemate.game.api.script.framework.tree.TreeTask;

public class ConstructionMission extends Mission implements ConfigurableTask {

    private final CraftTarget target;
    private TreeTask root;

    public ConstructionMission(CraftTarget target) {
        this.target = target;
        this.root = new GetToHouseTask(new BuildTask(target));
    }

    @Override
    public boolean hasEnded() {
        return false;
    }

    @Override
    public TreeTask getRoot() {
        return this.root;
    }

    @Override
    public void setRoot(TreeTask root) {
        this.root = root;
    }

    @Override
    public void select(String key) {

    }
}
