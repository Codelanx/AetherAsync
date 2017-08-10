package com.codelanx.aether.construction.mission.branch.build;

import com.codelanx.aether.construction.mission.CraftTarget;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.script.framework.tree.BranchTask;

public class ValidateTask extends BranchTask {

    private final CraftTarget target;
    private final BuildEvalTask success;
    private final RemoveTask remove;

    public ValidateTask(CraftTarget target) {
        this.target = target;
        this.success = new BuildEvalTask(this.target);
        this.remove = new RemoveTask(this.target);
    }

    @Override
    public BuildEvalTask successTask() {
        return this.success;
    }

    @Override
    public RemoveTask failureTask() {
        return this.remove;
    }

    @Override
    public boolean validate() {
        Environment.getLogger().info("\t=>ValidateTask");
        return GameObjects.newQuery().names(this.target.getBuildable().getName()).results().size() > 0;
    }
}
