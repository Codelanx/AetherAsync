package com.codelanx.aether.construction.mission.branch.build;

import com.codelanx.aether.construction.BasicBitchBot;
import com.codelanx.aether.construction.mission.Buildable;
import com.codelanx.aether.construction.mission.CraftTarget;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.framework.tree.LeafTask;
import com.runemate.game.api.script.framework.tree.TreeTask;

public class BuildInteractLeaf extends LeafTask {

    private final CraftTarget target;

    public BuildInteractLeaf(CraftTarget target) {
        this.target = target;
    }

    @Override
    public void execute() {
        if (Players.getLocal().getAnimationId() != -1) {
            return;
        }
        Buildable b = this.target.getBuildable();
        GameObject obj = GameObjects.newQuery().types(b.getType()).names(b.getName()).results().first();
        if (obj == null) {
            //back to the root for validation
            return;
        }
        if (obj.interact("Build")) {
            //exec us immediately, dirty hack/goto: BuildSelectorLeaf
            TreeTask target = BasicBitchBot.get().getBrain().getCurrentMission().getRoot().successTask();
            BasicBitchBot.get().getBrain().registerImmediate(target);
        }
    }
}
