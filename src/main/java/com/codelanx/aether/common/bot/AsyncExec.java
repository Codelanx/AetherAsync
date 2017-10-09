package com.codelanx.aether.common.bot;

import com.codelanx.commons.util.RNG;
import com.codelanx.commons.util.Scheduler;
import com.codelanx.commons.util.ref.Box;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public class AsyncExec {

    public static CompletableFuture<?> delayUntil(Supplier<Boolean> isTrue) {
        return AsyncExec.delayUntil(isTrue, TimeUnit.HOURS.toMillis(1)); //we'll be fair, if we're timed out for more than an hour it's time to stop lmao
    }

    public static CompletableFuture<?> delayUntil(Supplier<Boolean> isTrue, long timeoutMs) {
        return AsyncExec.delayUntil(isTrue, null, timeoutMs);
    }

    public static CompletableFuture<?> delayUntil(Supplier<Boolean> isTrue, Supplier<Boolean> resetTimer, long timeoutMS) {
        return AsyncExec.delayUntil(isTrue, resetTimer, Math.max(timeoutMS - 500, timeoutMS >> 1), timeoutMS);
    }

    public static CompletableFuture<?> delayUntil(Supplier<Boolean> isTrue, Supplier<Boolean> resetTimer, long minTimeoutMS, long maxTimeoutMS) {
        //make it on a random interval
        if (maxTimeoutMS < minTimeoutMS) {
            throw new IllegalArgumentException("Invalid timeout ranges (min: [0, max-1], max: [0, max]");
        }
        long timeoutMs = RNG.THREAD_LOCAL.current().nextLong(maxTimeoutMS - minTimeoutMS) + minTimeoutMS;
        CompletableFuture<Invalidator> compl = new CompletableFuture<>();
        Box<ScheduledFuture<?>> box = new Box<>();
        AtomicLong start = new AtomicLong(System.currentTimeMillis());
        box.value = Scheduler.runAsyncTaskRepeat(() -> {
            boolean cancelled = compl.isCancelled();
            if (cancelled) {
                box.value.cancel(true);
                return;
            }
            if (resetTimer != null && resetTimer.get()) {
                start.set(System.currentTimeMillis());
            }
            boolean timeout = start.get() + timeoutMs < System.currentTimeMillis();
            if (timeout || isTrue.get()) {
                compl.complete(timeout ? Invalidators.ALL : Invalidators.NONE);
                box.value.cancel(true);
            }
        }, 0, 50);
        Aether.getBot().getBrain().getLogicTree().delayUntil(compl);
        return compl;
    }


}
