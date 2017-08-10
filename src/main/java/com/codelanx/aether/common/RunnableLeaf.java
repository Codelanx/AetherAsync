package com.codelanx.aether.common;

import com.runemate.game.api.script.framework.tree.LeafTask;

public class RunnableLeaf extends LeafTask {

    private final Runnable task;

    private RunnableLeaf(Runnable task) {
        this.task = task;
    }

    @Override
    public void execute() {
        this.task.run();
    }

    public static RunnableLeaf of(Runnable task) {
        return new RunnableLeaf(task);
    }
}
