package com.codelanx.aether.common.bot.async;

import com.codelanx.commons.util.ref.Box;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class AsyncExec {

    public static CompletableFuture<?> delayUntil(Supplier<Boolean> isTrue) {
        CompletableFuture<?> compl = new CompletableFuture<>();
        Box<ScheduledFuture<?>> box = new Box<>();
        box.value = AetherAsyncBot.get().getScheduler().getThreadPool().scheduleAtFixedRate(() -> {
            if (compl.isCancelled()) {
                box.value.cancel(true);
                return;
            }
            if (isTrue.get()) {
                compl.complete(null);
                box.value.cancel(true);
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
        return compl;
    }
}
