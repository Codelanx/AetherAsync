package com.codelanx.aether.common.bot.async;

import com.codelanx.aether.common.bot.async.task.AetherTask;

import java.util.function.Supplier;

public class AetherTaskWrapper<T> extends AetherTask<T> {

    private AetherTask<T> child;

    public AetherTaskWrapper(AetherTask<T> child) {
        this.child = child;
    }

    public AetherTask<T> setChild(AetherTask<T> child) {
        AetherTask<T> old = this.child;
        this.child = child;
        return old;
    }

    @Override
    public AetherTask<?> getChild() {
        return this.child;
    }

    @Override
    public Supplier<T> getStateNow() {
        return () -> null;
    }
}
