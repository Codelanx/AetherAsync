package com.codelanx.aether.common.bot;

import com.codelanx.aether.common.bot.mission.AetherMission;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.commons.util.Reflections;
import com.codelanx.commons.util.Scheduler;
import com.codelanx.commons.util.ref.Box;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.AbstractBot.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class AetherBrain extends AetherTask<AetherTask<?>> {

    private static final long START_MS = System.currentTimeMillis();
    private final List<Runnable> immediateTasks = new ArrayList<>();
    private final List<AetherTask<?>> immediateRoot = new ArrayList<>();
    private final List<AetherMission<?>> nextMission = new ArrayList<>();
    private final LinkedList<AetherTask<?>> invalidationQueue = new LinkedList<>();
    private final Map<Class<?>, AetherTask<?>> taskMap = new HashMap<>();
    private final ReadWriteLock taskMapLock = new ReentrantReadWriteLock();
    private final List<CompletableFuture<Invalidator>> runningExecs = new ArrayList<>();
    private final AetherAsyncBot bot;
    private String lastThought;

    public AetherBrain(AetherAsyncBot bot) {
        this.bot = bot;
    }

    public AetherMission<?> getCurrentMission() {
        return this.nextMission.isEmpty() ? null : this.nextMission.get(0);
    }

    public void stroke() {
        this.immediateRoot.clear();
        this.immediateTasks.clear();
        this.nextMission.forEach(AetherTask::forceInvalidate);
        this.runningExecs.forEach(c -> c.cancel(true));
        this.runningExecs.clear();
    }

    public void lobotomy() {
        this.nextMission.clear();
        this.immediateRoot.clear();
        this.immediateTasks.clear();
        this.runningExecs.forEach(c -> c.cancel(true));
        this.runningExecs.clear();
    }
    
    public String getLastThought() {
        return this.lastThought;
    }
    
    private void setLastThought(String thought) {
        Environment.getLogger().info("[Brain] " + thought);
        this.lastThought = thought;
    }

    @Override
    public AetherTask<?> getChild(AetherTask<?> state) {
        return state;
    }

    private AetherTask<?> selectNextTask() {
        Runnable immediate = this.immediateTasks.isEmpty() ? null : this.immediateTasks.remove(0);
        if (immediate != null) {
            this.setLastThought("Running immediate task...");
            return AetherTask.of(immediate);
        }
        AetherTask<?> immediateRoot = this.immediateRoot.isEmpty() ? null : this.immediateRoot.remove(0);
        if (immediateRoot != null) {
            this.setLastThought("Running immediate AetherTask (" + immediateRoot.getTaskName() + ")...");
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
        this.setLastThought("[" + (System.currentTimeMillis() - START_MS) + "] Running mission: " + task.getTaskName());
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

    public AetherTask<?> getRememberedTask(Class<?> token) {
        return Reflections.operateLock(this.taskMapLock.readLock(), () -> this.taskMap.get(token));
    }

    //hints the brain to select the next direct target
    public void hint(Class<?> nextTarget) {

    }

    public void hint(Class<?> nextTarget, Object nextState) {
        AetherTask<?> task = Reflections.operateLock(this.taskMapLock.readLock(), () -> this.taskMap.get(nextTarget));
        if (task == null) {
            return;
        }
        this.registerImmediate(task);
        //TODO: what the fuck was this code, was I high? figure it out later
        /*
        Box<AetherTask<?>> reg = new Box<>();
        reg.value = AetherTask.of(() -> {
            if (task.isStateRetrieved()) {
                Object val = task.getStateNow().get();
                if (!Objects.equals(val, nextState)) {
                    //re-register, and try again
                    this.registerImmediate(reg.value);
                } else {
                    //we're good, roll ahead
                    //TODO: Retrieve child
                    return null;//reg.value.execute();
                }
            }
            return Invalidators.NONE;
        });
        this.registerImmediate(reg.value);*/
    }

    public void register(AetherMission<?> mission) {
        this.nextMission.add(mission);
    }

    public void registerImmediate(Runnable run) {
        this.registerImmediate(run, 0);
    }

    //TODO: Not use Execution
    public void registerImmediate(Runnable run, long delay) {
        this.immediateTasks.add(delay <= 0 ? run : () -> {
            run.run();
            Execution.delay(delay);
        });
    }

    public boolean isThinking() {
        if (this.runningExecs.isEmpty()) {
            return false;
        }
        CompletableFuture<Invalidator> inv = this.runningExecs.get(0);
        return !inv.isDone() && !inv.isCompletedExceptionally();
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

    @Override
    public boolean isSync() {
        return true;
    }

    void loop() {
        if (!this.runningExecs.isEmpty()) {
            Environment.getLogger().info("#BrainDebug currentTask not null");
            CompletableFuture<Invalidator> comp = this.runningExecs.get(0);
            if (comp.isCancelled()) {
                Environment.getLogger().info("#BrainDebug currentTask was cancelled, removing...");
                this.runningExecs.remove(0);
            } else if (comp.isDone() || comp.isCompletedExceptionally()) {
                Environment.getLogger().info("#BrainDebug currentTask done");
                try {
                    Environment.getLogger().info("#BrainDebug getting current task return value");
                    Invalidator inv = comp.get();
                    Environment.getLogger().info("#BrainDebug Invalidator: " + inv);
                    if (!inv.isNone()) {
                        Environment.getLogger().info("#BrainDebug invalidating...");
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
                        Environment.getLogger().info("#BrainDebug invalidated");
                    } else if (inv.isEnd()) {
                        Environment.getLogger().info("#BrainDebug Hit invalid state - ending bot");
                        this.bot.stop();
                        return;
                    } else {
                        this.invalidationQueue.stream().filter(AetherTask::invalidateAnyway).forEach(AetherTask::invalidate);
                        Environment.getLogger().info("#BrainDebug invalidation skipped");
                    }
                } catch (ExecutionException e) {
                    this.setLastThought("Error executing task for bot '" + this.bot.getClass().getSimpleName() + "':");
                    Environment.getLogger().info(Reflections.stackTraceToString(e));
                } catch (InterruptedException e) {
                    Environment.getLogger().severe("[Brain] Last task interrupted (shutting down?):");
                    Environment.getLogger().info(Reflections.stackTraceToString(e));
                    this.invalidationQueue.clear();
                    this.bot.stop();
                    return;
                } catch (Throwable t) {
                    Environment.getLogger().info("#BrainDebug totally uncaught exception wtf, how rude");
                    Environment.getLogger().info("#BrainDebug ex: " + Reflections.stackTraceToString(t));
                }
                this.invalidationQueue.clear();
                Environment.getLogger().info("#BrainDebug currentTask check done");
                this.runningExecs.remove(0);
            } else {
                Environment.getLogger().info("#BrainDebug currentTask not complete");
                return;
            }
        } else {
            this.invalidationQueue.clear();
        }
        /*if (!this.immediateTasks.isEmpty()) {
            Environment.getLogger().info("Running immediate task...");
            Runnable run = this.immediateTasks.remove(0);
            this.currentTask = CompletableFuture.supplyAsync(() -> {
                run.run();
                return null;
            });
        }*/
        this.setLastThought("#BrainDebug running root logic");
        AetherTask<?> root = this;
        StringBuilder prefix = new StringBuilder();
        while (!root.isExecutable()) {
            //this.setLastThought(prefix.toString() + "Validating task: " + root.getTaskName());
            if (!root.isStateRetrieved()) {
                //try again next tick
                Environment.getLogger().info("State not retrieved, checking later");
                this.invalidationQueue.clear();
                return;
            }
            //this.setLastThought(prefix.toString() + "State retrieved: " + root.getTaskName());
            AetherTask<?> next = null;
            try {
                next = root.getChild();
            } catch (ExecutionException e) {
                Environment.getLogger().info("Error retrieving child task:");
                Environment.getLogger().info(Reflections.stackTraceToString(e));
            } catch (InterruptedException e) {
                Environment.getLogger().severe("[Brain] Child task interrupted (shutting down?):");
                Environment.getLogger().info(Reflections.stackTraceToString(e));
                this.invalidationQueue.clear();
                this.bot.stop();
                return;
            }
            this.setLastThought(prefix.toString() + "Next child: " + Optional.ofNullable(next).map(AetherTask::getTaskName).orElse(null));
            if (next == null) {
                next = root.getChild(null); //default handling
                if (next == null) {
                    this.setLastThought("State hit null end in tree - stopping bot");
                    this.bot.stop();
                    this.invalidationQueue.clear();
                    return;
                }
            }
            prefix.append('\t');
            this.invalidationQueue.push(root);
            AetherTask<?> froot = root;
            Reflections.operateLock(this.taskMapLock.writeLock(), () -> {
                this.taskMap.putIfAbsent(froot.getToken(), froot);
            });
            root = next;
        }
        this.setLastThought("Executing task: " + root.getTaskName());
        //root should be executable now
        //due to the nature of runemate's api, we'll halt re-evaluation
        // until the task is executed
        AetherTask<?> froot = root;
        if (this.runningExecs.isEmpty()) {
            CompletableFuture<Invalidator> done = Scheduler.complete(froot::execute);
            if (this.runningExecs.isEmpty()) {
                this.runningExecs.add(done);
            } else {
                this.runningExecs.remove(0).cancel(true);
            }
        }
    }

    public void delayUntil(CompletableFuture<Invalidator> task) {
        this.runningExecs.add(0, task);
    }

}
