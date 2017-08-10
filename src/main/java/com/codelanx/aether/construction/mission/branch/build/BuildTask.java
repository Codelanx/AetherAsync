package com.codelanx.aether.construction.mission.branch.build;

import com.codelanx.aether.construction.mission.CraftTarget;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.script.framework.tree.BranchTask;

public class BuildTask extends BranchTask {

    private final ValidateTask opener;
    private final CraftTarget target;
    private final BuildSelectorLeaf build;

    public BuildTask(CraftTarget target) {
        this.target = target;
        this.build = new BuildSelectorLeaf(this.target);
        this.opener = new ValidateTask(this.target);
    }

    @Override
    public BuildSelectorLeaf successTask() {
        return this.build;
    }

    @Override
    public ValidateTask failureTask() {
        return this.opener;
    }

    @Override
    public boolean validate() {
        Environment.getLogger().info("BuildTask=>");
        return Interfaces.newQuery().containers(this.target.getParentId()).visible().results().size() > 0;
    }
}
