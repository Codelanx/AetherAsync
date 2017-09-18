package com.codelanx.aether.common.bot;

import com.codelanx.aether.common.bot.task.AetherTask;

import java.util.function.Supplier;
import java.util.stream.Stream;

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
    public boolean isSync() {
        return true;
    }

    @Override
    public AetherTask<?> getChild() {
        return this.child;
    }

    @Override
    public AetherTask<?> getChild(T state) {
        return this.getChild();
    }

    @Override
    public Stream<AetherTask<?>> getChildren() {
        return Stream.of(this.child);
    }

    @Override
    public Supplier<T> getStateNow() {
        return () -> null;
    }
}
