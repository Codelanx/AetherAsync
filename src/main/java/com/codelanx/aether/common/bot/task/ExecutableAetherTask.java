package com.codelanx.aether.common.bot.task;

import com.codelanx.aether.common.bot.Invalidator;
import com.codelanx.aether.common.bot.Invalidators;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ExecutableAetherTask extends AetherTask<Integer> {

    private final AtomicInteger executions = new AtomicInteger(0);
    private final Supplier<Boolean> task;
    private final Supplier<Invalidator> invTask;
    private final int expectedRuns;

    private ExecutableAetherTask(Supplier<Invalidator> inv) {
        this.invTask = inv;
        this.task = null;
        this.expectedRuns = Integer.MAX_VALUE;
    }

    private ExecutableAetherTask(Supplier<Boolean> task, int expectedRuns) {
        this.task = task;
        this.invTask = null;
        this.expectedRuns = expectedRuns;
    }

    protected ExecutableAetherTask() {
        this(Integer.MAX_VALUE);
    }

    protected ExecutableAetherTask(int expectedRuns) {
        this.task = null;
        this.invTask = null;
        this.expectedRuns = expectedRuns;
    }

    @Override
    public void onInvalidate() {
        super.invalidate();
        this.executions.set(0);
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    @Override
    public boolean isSync() {
        return super.isSync();
    }

    @Override
    public Invalidator execute(Integer state) {
        if (this.invTask == null && this.task == null && this.getClass() == ExecutableAetherTask.class) {
            throw new IllegalStateException("Cannot have an ExecutableAetherTask without any form of execution/override");
        }
        Invalidator back = Invalidators.NONE;
        if (this.invTask != null) {
            state = this.executions.incrementAndGet();
            back = this.invTask.get();
        } else if (this.task != null && this.task.get()) {
            state = this.executions.incrementAndGet();
        }
        if (state >= this.expectedRuns) {
            this.executions.set(0);
            return Invalidators.ALL;
        }
        return back;
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
