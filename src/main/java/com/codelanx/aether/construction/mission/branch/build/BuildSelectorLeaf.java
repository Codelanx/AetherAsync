package com.codelanx.aether.construction.mission.branch.build;

import com.codelanx.aether.common.CommonActions;
import com.codelanx.aether.construction.BasicBitchBot;
import com.codelanx.aether.construction.mission.CraftTarget;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceContainer;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceContainers;
import com.runemate.game.api.hybrid.queries.results.InterfaceComponentQueryResults;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.tree.LeafTask;
import com.runemate.game.api.script.framework.tree.TreeTask;

import java.util.Objects;

public class BuildSelectorLeaf extends LeafTask {

    private final CraftTarget target;

    public BuildSelectorLeaf(CraftTarget target) {
        this.target = target;
    }

    @Override
    public void execute() {
        Environment.getLogger().info("\t=>BuildSelectorLeaf");
        if (!InterfaceContainers.isLoaded(this.target.getParentId())) {
            Execution.delay(100);
            return;
        }
        InterfaceContainer cont = InterfaceContainers.getAt(this.target.getParentId());
        InterfaceComponentQueryResults res = cont.getComponents(i -> {
            return i != null && Objects.equals(this.target.getType(), i.getType()) && Objects.equals(this.target.getName(), i.getName());
        });
        if (res.isEmpty()) {
            Environment.getLogger().info("Could not find " + this.target.getName() + " container");
            CommonActions.END.getTask().execute();
            return;
        }
        if (!res.first().click()) {
            return;
        }
        this.target.getRequiredItems().forEach(i -> {
            BasicBitchBot.get().getInventory().update(i.getMaterial(), -i.getQuantity());
        });
        //exec us immediately, dirty hack/goto: RemoveTask
        TreeTask target = BasicBitchBot.get().getBrain().getCurrentMission().getRoot().failureTask().failureTask();
        BasicBitchBot.get().getBrain().registerImmediate(target);
    }
}
