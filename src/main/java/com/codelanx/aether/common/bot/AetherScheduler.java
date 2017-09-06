package com.codelanx.aether.common.bot;

import com.codelanx.commons.logging.Logging;
import com.codelanx.commons.util.Reflections;
import com.codelanx.commons.util.Scheduler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.logging.Level;

public class AetherScheduler {

    private static final long TICK_RATE_MS = 50;
    private static final AtomicInteger SCHEDULER_INDEX = new AtomicInteger();
    private static final AtomicInteger BOT_INDEX = new AtomicInteger();
    private final ScheduledThreadPoolExecutor botThread;
    private final ThreadGroup ourGroup;
    private final AsyncBot bot;
    private ScheduledFuture<?> runningBotThread;
    private final List<Future<?>> tasks = new ArrayList<>();
    private final ReadWriteLock tasksLock = new ReentrantReadWriteLock();

    AetherScheduler(AsyncBot bot) {
        this.bot = bot;
        this.ourGroup = Thread.currentThread().getThreadGroup();
        this.botThread = new ScheduledThreadPoolExecutor(1, this::newBotThread, (reject, scheduler) -> {
            Logging.warning("Bot task rejected for " + this.getClass().getSimpleName() + ", retrying..." );
            try {
                reject.run();
            } catch (Throwable t) {
                Logging.severe("Unhandled exception in " + this.getClass().getSimpleName() + ": " );
                Logging.severe(Reflections.stackTraceToString(t));
                AetherScheduler.this.bot.stop();
                return;
            }
        });
        Scheduler.setProvider(() -> {
            //TODO: Finish implementing
            /*ForkJoinPool workStealer = new ForkJoinPool(Runtime.getRuntime().availableProcessors() + 1,
                            pool -> new SpeshulThread(pool, AetherScheduler.this.ourGroup),
                            new UncaughtExceptionHandler() {
                                @Override
                                public void uncaughtException(Thread t, Throwable e) {
                                    Logging.info("Unhandled exception in worker thread " + t.getName() + ": " );
                                    Logging.info(Reflections.stackTraceToString(e));
                                    AetherScheduler.this.bot.stop();
                                }
                            }, true);
            CompletableFuture<?> comp = CompletableFuture.runAsync(() -> {}, workStealer);*/
            ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(2, this::newSchedulerThread, (reject, scheduler) -> {
                Logging.warning("Scheduler task rejected for " + this.getClass().getSimpleName() + ", retrying..." );
                try {
                    reject.run();
                } catch (Throwable t) {
                    Logging.severe("Unhandled exception in " + this.getClass().getSimpleName() + ": " );
                    Logging.severe(Reflections.stackTraceToString(t));
                    this.bot.stop();
                    return;
                }
            });
            pool.setRemoveOnCancelPolicy(true);
            return pool;
        });
    }

    private static class SpeshulThread extends ForkJoinWorkerThread {

        private static final Field setGroup = ((Supplier<Field>) (() -> {
            try {
                Field f = Thread.class.getDeclaredField("group");
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to reflect thread internals for fork-join pool", e);
            }
        })).get();
        private static final Method reserveThreadToGroup = ((Supplier<Method>) (() -> {
            try {
                Method m = ThreadGroup.class.getDeclaredMethod("addUnstarted");
                m.setAccessible(true);
                return m;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to reflect thread internals for fork-join pool", e);
            }
        })).get();
        private static final AtomicInteger workIndex = new AtomicInteger();


        public SpeshulThread(ForkJoinPool pool, ThreadGroup group) {
            super(pool);
            //A RACE
            //we need to reflect the threadgroup in the superclass before our constructor finishes
            try {
                setGroup.set(this, group);
                reserveThreadToGroup.invoke(group);
            } catch (IllegalAccessException | InvocationTargetException e) {
                Logging.log(Level.SEVERE, "Failed to register our task to the correct botgroup, runemate gun b mad", e);
                Aether.getScheduler().getBotThread().execute(Aether.getBot()::stop);
                return;
            }
            this.setName("aether-work-pool-" + SpeshulThread.workIndex.getAndIncrement());
        }
    }

    void register(AsyncBot bot) {
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

    void resume(AsyncBot bot) {
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
