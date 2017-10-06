package com.codelanx.aether.common.bot;

import com.codelanx.commons.util.Scheduler;
import com.codelanx.commons.util.ref.Box;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Supplier;

public class AsyncExec {

    public static CompletableFuture<?> delayUntil(Supplier<Boolean> isTrue) {
        CompletableFuture<Invalidator> compl = new CompletableFuture<>();
        Box<ScheduledFuture<?>> box = new Box<>();
        box.value = Scheduler.runAsyncTaskRepeat(() -> {
            if (compl.isCancelled()) {
                box.value.cancel(true);
                return;
            }
            if (isTrue.get()) {
                compl.complete(Invalidators.NONE);
                box.value.cancel(true);
            }
        }, 0, 50);
        Aether.getBot().getBrain().getLogicTree().delayUntil(compl);
        return compl;
    }

    public static CompletableFuture<?> delayUntil(Supplier<Boolean> isTrue, long timeoutMs) {
        CompletableFuture<Invalidator> compl = new CompletableFuture<>();
        Box<ScheduledFuture<?>> box = new Box<>();
        box.value = Scheduler.runAsyncTaskRepeat(() -> {
            if (compl.isCancelled()) {
                box.value.cancel(true);
                return;
            }
            if (isTrue.get()) {
                compl.complete(Invalidators.NONE);
                box.value.cancel(true);
            }
        }, 0, 50);
        Aether.getBot().getBrain().getLogicTree().delayUntil(compl);
        return compl;
    }
}
