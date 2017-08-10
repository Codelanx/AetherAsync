package com.codelanx.aether.construction.mission.branch.build;

import com.codelanx.aether.common.Brain;
import com.codelanx.aether.construction.BasicBitchBot;
import com.codelanx.aether.common.RunnableLeaf;
import com.codelanx.aether.common.menu.dialog.DialogueIterator;
import com.codelanx.aether.construction.mission.CraftTarget;
import com.codelanx.aether.construction.mission.Destructable;
import com.codelanx.aether.construction.mission.branch.bank.ButlerSpeakTask;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.LeafTask;
import com.runemate.game.api.script.framework.tree.TreeTask;

import java.util.Objects;

public class RemoveTask extends BranchTask {

    private final CraftTarget target;
    private final TreeTask success;

    public RemoveTask(CraftTarget target) {
        this.target = target;
        this.success = new LeafTask() {
            @Override
            public void execute() {
                Environment.getLogger().info("[RemoveTask] Current dialogue: " + DialogueIterator.getCurrentDialogue());
                if (ButlerSpeakTask.isButlerSpeaking() && target.getPossibleBuilds() <= 0) {
                    Environment.getLogger().info("Interrupting butler!");
                    Brain b = BasicBitchBot.get().getBrain();
                    TreeTask speakTask = b.getCurrentMission().failureTask().successTask().failureTask().successTask();
                    b.registerImmediate(speakTask);
                    return;
                }
                if (ChatDialog.getOption(1) != null) {
                    Environment.getLogger().info("[RemoveTask] CONFIRMING");
                    if (ChatDialog.getOption(1).select()) {
                        Environment.getLogger().info("[RemoveTask] CONFIRMED");
                        //sanity check
                        Execution.delay(25);
                        if (Objects.equals(ChatDialog.getTitle(), "Really remove it?")) {
                            //we failed!
                            Environment.getLogger().info("Failed to remove, re-running");
                            return;
                        }
                        //exec us immediately, dirty hack/goto: BuildEvalTask
                        TreeTask target = BasicBitchBot.get().getBrain().getCurrentMission().getRoot().failureTask().successTask();
                        BasicBitchBot.get().getBrain().registerImmediate(target);
                    }
                } else {
                    Destructable d = RemoveTask.this.target.getBuildable().getResult();
                    GameObject obj = GameObjects.newQuery().names(d.getName()).results().first();
                    if (obj != null) {
                        obj.interact("Remove");
                        Environment.getLogger().info("[RemoveTask] REMOVING");
                    }
                    //Execution.delay(200 + (long) (ThreadLocalRandom.current().nextGaussian() * 35));
                }
            }
        };
    }

    @Override
    public TreeTask successTask() {
        return this.success;
    }

    @Override
    public TreeTask failureTask() {
        if (Players.getLocal().getAnimationId() != -1) {
            BasicBitchBot.get().getBrain().registerImmediate(this);
        }
        return RunnableLeaf.of(() -> Execution.delayUntil(this::rawValidate, 600, 1200));
    }

    @Override
    public boolean validate() {
        Environment.getLogger().info("\t\tRemoveTask=>");
        return this.rawValidate();
    }

    private boolean rawValidate() {
        Destructable obj = this.target.getBuildable().getResult();
        return GameObjects.newQuery().names(obj.getName()).types(obj.getType()).results().size() > 0;
    }


}
