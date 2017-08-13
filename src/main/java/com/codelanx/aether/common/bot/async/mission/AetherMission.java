package com.codelanx.aether.common.bot.async.mission;

import com.codelanx.aether.common.bot.async.AetherTaskWrapper;
import com.codelanx.aether.common.bot.async.task.AetherTask;

import java.util.function.Supplier;

public abstract class AetherMission<E> extends AetherTaskWrapper<E> {

    public AetherMission(AetherTask<E> root) {
        super(root);
    }

    public abstract boolean hasEnded();

    public static <T> AetherMission<T> of(AetherTask<T> root) {
        return new AetherMission<T>(root) {
            @Override
            public Supplier<T> getStateNow() {
                return () -> null;
            }

            @Override
            public boolean hasEnded() {
                return false;
            }
        };
    }
}
