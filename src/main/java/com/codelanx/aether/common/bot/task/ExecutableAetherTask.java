package com.codelanx.aether.common.bot.task;

import com.codelanx.aether.common.bot.Invalidator;
import com.codelanx.aether.common.bot.Invalidators;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ExecutableAetherTask extends AetherTask<Integer> {

    private final AtomicInteger executions = new AtomicInteger(0);
    private final Supplier<Boolean> task;
    private final int expectedRuns;

    private ExecutableAetherTask(Supplier<Boolean> task, int expectedRuns) {
        this.task = task;
        this.expectedRuns = expectedRuns;
    }

    @Override
    public void onInvalidate() {
        super.invalidate();
        this.executions.set(0);
    }

    @Override
    public Invalidator execute(Integer state) {
        if (this.task.get()) {
            state = this.executions.incrementAndGet();
        }
        if (state >= this.expectedRuns) {
            this.executions.set(0);
            return Invalidators.ALL;
        }
        return Invalidators.NONE;
    }

    @Override
    public Supplier<Integer> getStateNow() {
        return this.executions::get;
    }

    public static ExecutableAetherTask of(Runnable cmd, int expectedRuns) {
        return ExecutableAetherTask.ofFailable(() -> {
            cmd.run();
            return true;
        }, expectedRuns);
    }

    public static ExecutableAetherTask ofFailable(Supplier<Boolean> cmd, int expectedRuns) {
        return new ExecutableAetherTask(cmd, expectedRuns);
    }

    public static ExecutableAetherTask of(Runnable cmd) {
        return ExecutableAetherTask.of(cmd, 1);
    }

    public static ExecutableAetherTask ofFailable(Supplier<Boolean> cmd) {
        return ExecutableAetherTask.ofFailable(cmd, 1);
    }
}
