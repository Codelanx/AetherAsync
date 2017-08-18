package com.codelanx.aether.common.bot;

import com.codelanx.commons.util.Reflections;
import com.codelanx.commons.util.Scheduler;
import com.runemate.game.api.hybrid.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class AetherScheduler {

    private static final long TICK_RATE_MS = 50;
    private static final AtomicInteger SCHEDULER_INDEX = new AtomicInteger();
    private static final AtomicInteger BOT_INDEX = new AtomicInteger();
    private final ScheduledThreadPoolExecutor botThread;
    private final ThreadGroup ourGroup;
    private final AetherAsyncBot bot;
    private ScheduledFuture<?> runningBotThread;
    private final List<Future<?>> tasks = new ArrayList<>();
    private final ReadWriteLock tasksLock = new ReentrantReadWriteLock();

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
        Scheduler.setProvider(() -> {
            ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(2, this::newSchedulerThread, (reject, scheduler) -> {;
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
            pool.setRemoveOnCancelPolicy(true);
            return pool;
        });
    }

    void register(AetherAsyncBot bot) {
        if (this.runningBotThread != null) {
            throw new IllegalStateException("Already registered bot to scheduler");
        }
        this.runningBotThread = this.botThread.scheduleAtFixedRate(bot::loop, 500, TICK_RATE_MS, TimeUnit.MILLISECONDS);
    }

    void pause() {
        //TODO: determine safety
        if (this.runningBotThread == null) {
            return;
        }
        this.runningBotThread.cancel(true);
        Scheduler.cancelAllTasks();
        this.runningBotThread = null;
    }

    void resume(AetherAsyncBot bot) {
        this.register(bot);
    }

    boolean isShutdown() {
        return this.botThread.isShutdown() || Scheduler.getService().isShutdown();
    }

    void stop() {
        //TODO: Thread check, we should probably have this called from the main bot thread
        Scheduler.cancelAndShutdown();
        this.botThread.shutdown();
    }

    private Thread newBotThread(Runnable r) {
        return new Thread(this.ourGroup, r,"Aether-primary-" + this.bot.getClass().getName() + "-" + BOT_INDEX.getAndIncrement());
    }

    private Thread newSchedulerThread(Runnable r) {
        return new Thread(this.ourGroup, r,"Aether-scheduler-" + this.bot.getClass().getName() + "-" + SCHEDULER_INDEX.getAndIncrement());
    }

    public ScheduledThreadPoolExecutor getThreadPool() {
        return (ScheduledThreadPoolExecutor) Scheduler.getService();
    }

    public <R> CompletableFuture<R> complete(Supplier<R> supplier) {
        CompletableFuture<R> back = CompletableFuture.supplyAsync(supplier, this.getThreadPool());
        this.addTask(back);
        return back;
    }

    private void addTask(Future<?> future) {
        Reflections.operateLock(this.tasksLock.writeLock(), () -> {
            this.tasks.add(future);
        });
    }

    ScheduledThreadPoolExecutor getBotThread() {
        return this.botThread;
    }
}
