package com.codelanx.aether.common.bot.async;

import com.codelanx.aether.common.bot.async.mission.AetherMission;
import com.codelanx.aether.common.bot.async.task.AetherTask;
import com.codelanx.commons.util.Reflections;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.AbstractBot;
import com.runemate.game.api.script.framework.AbstractBot.State;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class AetherBrain extends AetherTask<AetherTask<?>> {

    private static final long START_MS = System.currentTimeMillis();
    private final List<Runnable> immediateTasks = new ArrayList<>();
    private final List<AetherTask<?>> immediateRoot = new ArrayList<>();
    private final List<AetherMission<?>> nextMission = new ArrayList<>();
    private final LinkedList<AetherTask<?>> invalidationQueue = new LinkedList<>();
    private volatile CompletableFuture<Invalidator> currentTask = null;
    private final AetherAsyncBot bot;

    public AetherBrain(AetherAsyncBot bot) {
        this.bot = bot;
    }

    public AetherMission getCurrentMission() {
        return this.nextMission.isEmpty() ? null : this.nextMission.get(0);
    }

    public void lobotomy() {
        this.nextMission.clear();
        this.immediateRoot.clear();
        this.immediateTasks.clear();
    }

    @Override
    public AetherTask<?> getChild(AetherTask<?> state) {
        return state;
    }

    private AetherTask<?> selectNextTask() {
        Runnable immediate = this.immediateTasks.isEmpty() ? null : this.immediateTasks.remove(0);
        if (immediate != null) {
            Environment.getLogger().info("[Brain] Running immediate task...");
            return AetherTask.of(immediate);
        }
        AetherTask<?> immediateRoot = this.immediateRoot.isEmpty() ? null : this.immediateRoot.remove(0);
        if (immediateRoot != null) {
            Environment.getLogger().info("[Brian] Running immediate TreeTask (" + immediateRoot.getClass().getSimpleName() + ")...");
            return immediateRoot;
        }
        AetherMission<?> task = this.nextMission.get(0);
        if (task.hasEnded()) {
            this.nextMission.remove(0);
            if (this.nextMission.isEmpty()) {
                if (this.bot.getState() == State.UNSTARTED) {
                    return AetherTask.NOTHING;
                }
                //TODO: Proper Failure
                return null;
            }
            task = this.nextMission.get(0);
        }
        Environment.getLogger().info("[" + (System.currentTimeMillis() - START_MS) + "] Running mission: " + task.getClass().getSimpleName());
        return task;
    }

    @Override
    public Supplier<AetherTask<?>> getStateNow() {
        return this::selectNextTask;
    }

    @Override
    public boolean isExecutable() {
        return false;
    }

    //hints the brain to select the next direct target
    public void hint(Class<?> nextTarget) {

    }

    public void register(AetherMission<?> mission) {
        this.nextMission.add(mission);
    }

    public void registerImmediate(Runnable run) {
        this.registerImmediate(run, 0);
    }

    public void registerImmediate(Runnable run, long delay) {
        this.immediateTasks.add(delay <= 0 ? run : () -> {
            run.run();
            Execution.delay(delay);
        });
    }

    public void forget(AetherMission<?> m) {
        this.nextMission.remove(m);
    }

    public AetherMission<?> popMission() {
        //this.forget(this.getCurrentMission());
        if (!this.nextMission.isEmpty()) {
            return this.nextMission.remove(0);
        }
        throw new NoSuchElementException("Cannot pop an empty brain - it's dead Negan!");
    }

    public void registerImmediate(AetherTask<?> task) {
        Environment.getLogger().info("Registering immediate AetherTask[" + this.immediateRoot.size() + "]: " + task.getClass().getSimpleName());
        this.immediateRoot.add(task);
    }

    void loop() {
        AetherTask<?> root = this;
        StringBuilder prefix = new StringBuilder();
        while (!root.isExecutable()) {
            Environment.getLogger().info(prefix.toString() + "Validating task: " + root.getClass().getName());
            if (!root.isStateRetrieved()) {
                //try again next tick
                Environment.getLogger().info("State not retrieved, checking later");
                this.invalidationQueue.clear();
                return;
            }
            AetherTask<?> next = null;
            try {
                next = root.getChild();
            } catch (ExecutionException | InterruptedException e) {
                Environment.getLogger().info("Error retrieving child task:");
                Environment.getLogger().info(Reflections.stackTraceToString(e));
            }
            if (next == null) {
                next = root.getChild(null); //default handling
                if (next == null) {
                    Environment.getLogger().info("State hit null end in tree - stopping bot");
                    this.bot.stop();
                    this.invalidationQueue.clear();
                    return;
                }
            }
            prefix.append('\t');
            this.invalidationQueue.push(root);
            root = next;
        }
        Environment.getLogger().info("Executing task: " + root.getClass().getName());
        //root should be executable now
        //due to the nature of runemate's api, we'll halt re-evaluation
        // until the task is executed
        AetherTask<?> froot = root;
        if (this.currentTask == null) {
            CompletableFuture<Invalidator> done = CompletableFuture.supplyAsync(froot::execute, this.bot.getScheduler().getThreadPool());
            if (this.currentTask == null) {
                this.currentTask = done;
            } else {
                done.cancel(true);
            }
        }
        if (this.currentTask.isDone() || this.currentTask.isCompletedExceptionally()) {
            try {
                Invalidator inv = this.currentTask.get();
                if (!inv.isNone()) {
                    Stream<AetherTask<?>> auto = inv.isAll() ? Stream.empty() : this.invalidationQueue.stream().filter(AetherTask::invalidateAnyway);
                    Stream<AetherTask<?>> invalidate;
                    if (inv.isRange()) {
                        invalidate = this.invalidationQueue.subList(0, inv.getRange()).stream();
                    } else {
                        if (inv.isAll()) {
                            invalidate = this.invalidationQueue.stream();
                        } else {
                            invalidate = Stream.empty();
                        }
                    }
                    Stream.concat(auto, invalidate).distinct().forEach(AetherTask::invalidate);
                }
            } catch (InterruptedException | ExecutionException e) {
                Environment.getLogger().info("Error executing task for bot '" + this.getClass().getSimpleName() + "':");
                Environment.getLogger().info(Reflections.stackTraceToString(e));
            }
            this.invalidationQueue.clear();
        }
    }

}
