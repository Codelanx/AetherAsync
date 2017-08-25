package com.codelanx.aether.common.branch;

import com.codelanx.aether.common.bot.Invalidator;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.Environment;

import java.util.function.Supplier;

public enum CommonTasks implements Supplier<Invalidator> {

    END(() -> Environment.getBot().stop()),
    ;

    private final AetherTask<?> task;

    private CommonTasks(Runnable task) {
        this.task = AetherTask.of(task);
    }

    private CommonTasks(Supplier<Boolean> task) {
        this.task = AetherTask.ofRunemateFailable(task);
    }

    //TODO: Unfuck this
    @Override
    public Invalidator get() {
        return this.task.execute(null);
    }

    public AetherTask<?> asTask() {
        return this.task;
    }
}
