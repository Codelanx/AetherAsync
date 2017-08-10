package com.codelanx.aether.construction.mission.branch.build;

import com.codelanx.aether.common.RunnableLeaf;
import com.codelanx.aether.construction.mission.CraftTarget;
import com.codelanx.aether.construction.mission.branch.bank.ButlerTask;
import com.codelanx.aether.construction.mission.branch.bank.ButlerSpeakTask;
import com.codelanx.aether.construction.mission.branch.bank.ButlerSpeakTask.State;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.TreeTask;


public class BuildEvalTask extends BranchTask {

    private final CraftTarget target;
    private final ButlerTask failure = new ButlerTask();
    private final TreeTask success;
    private final boolean callButlerBeforeLastBuild = true;

    public BuildEvalTask(CraftTarget target) {
        this.target = target;
        this.success = new BuildInteractLeaf(this.target);
    }

    @Override
    public TreeTask successTask() {
        return success;
    }

    @Override
    public TreeTask failureTask() {
        if (this.callButlerBeforeLastBuild) {
            ButlerSpeakTask bst = this.failure.successTask();
            if (bst.getSpeakingState() == State.WAITING_DELIVERY && ButlerSpeakTask.findButler() == null) {
                return RunnableLeaf.of(() -> Execution.delayUntil(() -> {
                    return ButlerSpeakTask.findButler() != null;
                }));
            }
        }
        return failure;
    }

    @Override
    public boolean validate() {
        Environment.getLogger().info("\t\t=>BuildEvalTask");
        int builds = this.target.getPossibleBuilds();
        if (this.callButlerBeforeLastBuild) {
            ButlerSpeakTask bst = this.failure.successTask();
            if (bst.getSpeakingState() == State.GETTING_DELIVERY) {
                return builds > 1;
            } else {
                return builds > 0; //TODO: Delay at this point
            }
        }
        return builds > 0;
    }
}
