package com.codelanx.aether.common.branch;

import com.codelanx.aether.common.bot.task.AetherTask;

import java.util.function.Supplier;

public class BiTask extends AetherTask<Boolean> {

    private final Supplier<Boolean> divider;

    public BiTask(Supplier<Boolean> divider, AetherTask<?> ifTrue, AetherTask<?> ifFalse) {
        this.divider = divider;
        this.register(true, ifTrue);
        this.register(false, ifFalse);
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return this.divider;
    }
}
