package com.codelanx.aether.common.bot.async;

import com.codelanx.commons.util.Reflections;
import com.runemate.game.api.hybrid.Environment;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AetherScheduler {

    private static final long TICK_RATE_MS = 50;
    private static final AtomicInteger SCHEDULER_INDEX = new AtomicInteger();
    private static final AtomicInteger BOT_INDEX = new AtomicInteger();
    private final ScheduledThreadPoolExecutor scheduler;
    private final ScheduledThreadPoolExecutor botThread;
    private final ThreadGroup ourGroup;
    private final AetherAsyncBot bot;
    private ScheduledFuture<?> runningBotThread;
    private final CompletableFuture<?> ended;

    AetherScheduler(AetherAsyncBot bot) {
        this.bot = bot;
        this.ourGroup = Thread.currentThread().getThreadGroup();
        this.botThread = new ScheduledThreadPoolExecutor(1, this::newBotThread, (reject, scheduler) -> {
            Environment.getLogger().info("Bot task rejected for " + this.getClass().getSimpleName() + ", retrying..." );
            try {
                reject.run();
            } catch (Throwable t) {
                Environment.getLogger().info("Unhandled exception in " + this.getClass().getSimpleName() + ": " );
                Environment.getLogger().info(Reflections.stackTraceToString(t));
                AetherScheduler.this.bot.stop();
                return;
            }
        });
        this.scheduler = new ScheduledThreadPoolExecutor(2, this::newSchedulerThread, (reject, scheduler) -> {;
            Environment.getLogger().info("Scheduler task rejected for " + this.getClass().getSimpleName() + ", retrying..." );
            try {
                reject.run();
            } catch (Throwable t) {
                Environment.getLogger().info("Unhandled exception in " + this.getClass().getSimpleName() + ": " );
                Environment.getLogger().info(Reflections.stackTraceToString(t));
                this.bot.stop();
                return;
            }
        });
        this.scheduler.setRemoveOnCancelPolicy(true);
        this.scheduler.setRemoveOnCancelPolicy(true);
        this.ended = new CompletableFuture<>();
    }

    void register(AetherAsyncBot bot) {
        if (this.runningBotThread != null) {
            throw new IllegalStateException("Already registered bot to scheduler");
        }
        this.runningBotThread = this.botThread.scheduleAtFixedRate(bot::loop, 5, TICK_RATE_MS, TimeUnit.MILLISECONDS);
    }

    void pause() {
        //TODO: determine safety
        if (this.runningBotThread == null) {
            return;
        }
        this.runningBotThread.cancel(true);
        this.runningBotThread = null;
    }

    void resume(AetherAsyncBot bot) {
        this.register(bot);
    }

    boolean isShutdown() {
        return this.botThread.isShutdown() || this.scheduler.isShutdown();
    }

    void stop() {
        try {
            this.scheduler.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            this.scheduler.shutdownNow();
        }
        this.scheduler.shutdown();
        this.botThread.shutdown();
    }

    private Thread newBotThread(Runnable r) {
        return new Thread(this.ourGroup, r,"Aether-primary-" + this.bot.getClass().getName() + "-" + BOT_INDEX.getAndIncrement());
    }

    private Thread newSchedulerThread(Runnable r) {
        return new Thread(this.ourGroup, r,"Aether-scheduler-" + this.bot.getClass().getName() + "-" + SCHEDULER_INDEX.getAndIncrement());
    }

    public ScheduledExecutorService getThreadPool() {
        return this.scheduler;
    }

    ScheduledThreadPoolExecutor getBotThread() {
        return this.botThread;
    }
}
