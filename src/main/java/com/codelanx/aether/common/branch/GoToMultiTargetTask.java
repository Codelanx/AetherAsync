package com.codelanx.aether.common.branch;

import com.codelanx.aether.common.bot.task.AetherTask;

import java.util.function.Supplier;

public class GoToMultiTargetTask extends GoToTargetTask {
    public GoToMultiTargetTask(Supplier target, AetherTask arrival) {
        super(target, arrival);
    }
}
