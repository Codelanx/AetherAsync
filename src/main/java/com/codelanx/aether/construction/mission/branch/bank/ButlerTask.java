package com.codelanx.aether.construction.mission.branch.bank;

import com.codelanx.aether.common.CommonActions;
import com.codelanx.aether.common.menu.dialog.DialogueIterator;
import com.codelanx.aether.construction.ConstructionActions;
import com.codelanx.aether.common.menu.dialog.Dialogue;
import com.codelanx.aether.construction.mission.branch.bank.ButlerSpeakTask.State;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.TreeTask;

public class ButlerTask extends BranchTask {

    private final ButlerSpeakTask speaker = new ButlerSpeakTask();
    private Coordinate lastCoord;

    @Override
    public ButlerSpeakTask successTask() {
        return this.speaker;
    }

    @Override
    public TreeTask failureTask() {
        return this.speaker.getSpeakingState() != State.GETTING_DELIVERY
                ? ButlerSpeakTask.findButler() != null
                    ? ConstructionActions.MOVE_TO_BUTLER.getTask()
                    : CommonActions.WAIT.getTask()
                : ConstructionActions.MOVE_TO_BUTLER.getTask();
    }

    @Override
    public boolean validate() {
        Environment.getLogger().info("\t\t\t=>ButlerTask");
        Dialogue d = DialogueIterator.getCurrentDialogue();
        boolean back = !d.isEmpty();
        if (back) {
            Environment.getLogger().info("Current dialogue: " + d);
        }
        return back;
    }
}
