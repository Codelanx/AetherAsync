package com.codelanx.aether.common.bot.async.task;

import java.util.Optional;

public class HashedTaskState<E> {

    private final E task;

    public HashedTaskState(E task) {
        this.task = task;
    }

    @Override
    public int hashCode() {
        if (this.task == null) {
            return 0;
        }
        return task.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HashedTaskState)) {
            return false;
        }
        HashedTaskState<?> other = (HashedTaskState<?>) obj;
        if (this.task == null) {
            return other.task == null;
        }
        return task.equals(other.task);
    }

    @Override
    public String toString() {
        return "HashedTaskState{" + Optional.ofNullable(this.task).map(Object::toString).orElse(null) + "}";
    }
}
