package com.codelanx.aether.common.mission;

import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.TreeTask;

/**
 * A mission is a branch task, representative of a higher-level, more
 * abstract goal (e.g. "banking", "go train"). These are typically
 * recipients of a branch "jump", or a movement in the branch logic.
 *
 * Missions exist to represent a logical loop in a tree-based structure
 * (a grid, effectively). A mission will loop upon itself utilizing the
 * {@link #failureTask()} call, and upon a successful validation will
 * proceed to the next mission (which will be determined based on current
 * results, thus sent to a validator).
 */
public abstract class Mission extends BranchTask {

    @Override
    public boolean validate() {
        this.onIteration();
        return this.getRoot().validate();
    }

    @Override
    public TreeTask successTask() {
        return this.getRoot().successTask();
    }

    @Override
    public TreeTask failureTask() {
        return this.getRoot().failureTask();
    }

    public abstract boolean hasEnded();

    public abstract TreeTask getRoot();

    public abstract void setRoot(TreeTask task);

    public void onIteration() {

    }

    public static Mission of(TreeTask root) {
        return new Mission() {

            private TreeTask r = root;

            @Override
            public boolean hasEnded() {
                return false;
            }

            @Override
            public TreeTask getRoot() {
                return this.r;
            }

            @Override
            public void setRoot(TreeTask root) {
                this.r = root;
            }
        };
    }
}
