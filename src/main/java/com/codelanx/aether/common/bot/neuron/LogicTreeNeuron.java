package com.codelanx.aether.common.bot.neuron;

import com.codelanx.aether.common.bot.Brain;
import com.codelanx.aether.common.bot.Invalidator;
import com.codelanx.aether.common.bot.mission.Mission;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.commons.logging.Logging;
import com.codelanx.commons.util.Reflections;
import com.codelanx.commons.util.Scheduler;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.AbstractBot.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//TODO: Import brain logic
public class LogicTreeNeuron extends Neuron {

    private static final long START_MS = System.currentTimeMillis();
    private final List<Runnable> immediateTasks = new ArrayList<>();
    private final List<AetherTask<?>> immediateRoot = new ArrayList<>();
    private final List<Mission<?>> nextMission = new ArrayList<>();
    private final LinkedList<AetherTask<?>> invalidationQueue = new LinkedList<>();
    private final Map<Class<?>, AetherTask<?>> taskMap = new HashMap<>();
    private final ReadWriteLock taskMapLock = new ReentrantReadWriteLock();
    private final List<CompletableFuture<Invalidator>> runningExecs = new ArrayList<>();
    private String lastThought;

    @Override
    public boolean applies() {
        return !this.nextMission.isEmpty() || !this.immediateRoot.isEmpty() || !this.immediateTasks.isEmpty();
    }

    //invalidates and removes immediate tasks
    public void invalidate() {
        this.immediateRoot.clear();
        this.immediateTasks.clear();
        this.nextMission.forEach(AetherTask::forceInvalidate);
        this.runningExecs.forEach(c -> c.cancel(true));
        this.runningExecs.clear();
    }

    @Override
    public boolean isBlocking() {
        return this.applies();
    }

    @Override
    public boolean isEvaluationSkipped() {
        return this.isThinking();
    }

    //removes all tasks and missions
    public void clear() {
        this.nextMission.clear();
        this.immediateRoot.clear();
        this.immediateTasks.clear();
        this.runningExecs.forEach(c -> c.cancel(true));
        this.runningExecs.clear();
    }

    public boolean isThinking() {
        if (this.runningExecs.isEmpty()) {
            return false;
        }
        CompletableFuture<Invalidator> inv = this.runningExecs.get(0);
        return !inv.isDone() && !inv.isCompletedExceptionally();
    }

    @Override
    public void fire(Brain brain) {
        if (this.isThinking()) {
            return;
        }
        if (!this.runningExecs.isEmpty()) {
            Logging.info("#BrainDebug currentTask not null");
            CompletableFuture<Invalidator> comp = this.runningExecs.get(0);
            if (comp.isCancelled()) {
                Logging.info("#BrainDebug currentTask was cancelled, removing...");
                this.runningExecs.remove(0);
            } else if (comp.isDone() || comp.isCompletedExceptionally()) {
                Logging.info("#BrainDebug currentTask done");
                try {
                    Logging.info("#BrainDebug getting current task return value");
                    Invalidator inv = comp.get();
                    Logging.info("#BrainDebug Invalidator: " + inv);
                    if (!inv.isNone()) {
                        Logging.info("#BrainDebug invalidating...");
                        if (inv.isRange()) {
                            Iterator<AetherTask<?>> itr = this.invalidationQueue.iterator();
                            for (int i = 0; i < inv.getRange() && itr.hasNext(); i++) {
                                itr.next().invalidate();
                                itr.remove();
                            }
                        } else {
                            //invalidate all
                            this.invalidationQueue.forEach(AetherTask::invalidate);
                            this.invalidationQueue.clear();
                        }
                    } else if (inv.isEnd()) {
                        Logging.info("#BrainDebug Hit invalid state - ending bot");
                        brain.getBot().stop();
                        return;
                    } else {
                        this.invalidationQueue.stream().filter(AetherTask::invalidateAnyway).forEach(AetherTask::invalidate);
                        Logging.info("#BrainDebug invalidation skipped");
                    }
                } catch (ExecutionException e) {
                    this.setLastThought("Error executing task for bot '" + brain.getBot().getClass().getSimpleName() + "':");
                    Logging.info(Reflections.stackTraceToString(e));
                } catch (InterruptedException e) {
                    Logging.severe("[Brain] Last task interrupted (shutting down?):");
                    Logging.info(Reflections.stackTraceToString(e));
                    this.invalidationQueue.clear();
                    brain.getBot().stop();
                    return;
                } catch (Throwable t) {
                    Logging.info("#BrainDebug totally uncaught exception wtf, how rude");
                    Logging.info("#BrainDebug ex: " + Reflections.stackTraceToString(t));
                }
                this.invalidationQueue.clear();
                Logging.info("#BrainDebug currentTask check done");
                this.runningExecs.remove(0);
            } else {
                Logging.info("#BrainDebug currentTask not complete");
                return;
            }
        } else {
            this.invalidationQueue.clear();
        }
        /*if (!this.immediateTasks.isEmpty()) {
            Logging.info("Running immediate task...");
            Runnable run = this.immediateTasks.remove(0);
            this.currentTask = CompletableFuture.supplyAsync(() -> {
                run.run();
                return null;
            });
        }*/
        this.setLastThought("#BrainDebug running root logic");
        AetherTask<?> root = this.selectNextTask(brain);
        if (root == null) {
            //TODO: stop bot
            return;
        }
        StringBuilder prefix = new StringBuilder();
        while (!root.isExecutable()) {
            //this.setLastThought(prefix.toString() + "Validating task: " + root.getTaskName());
            if (!root.isStateRetrieved()) {
                //try again next tick
                Logging.info("State not retrieved, checking later");
                this.invalidationQueue.clear();
                return;
            }
            //this.setLastThought(prefix.toString() + "State retrieved: " + root.getTaskName());
            AetherTask<?> next = null;
            try {
                next = root.getChild();
            } catch (ExecutionException e) {
                Logging.info("Error retrieving child task:");
                Logging.info(Reflections.stackTraceToString(e));
            } catch (InterruptedException e) {
                Logging.severe("[Brain] Child task interrupted (shutting down?):");
                Logging.info(Reflections.stackTraceToString(e));
                this.invalidationQueue.clear();
                brain.getBot().stop();
                return;
            }
            this.setLastThought(prefix.toString() + "Next child: " + Optional.ofNullable(next).map(AetherTask::getTaskName).orElse(null));
            if (next == null) {
                next = root.getChild(null); //default handling
                if (next == null) {
                    this.setLastThought("State hit null end in tree - stopping bot");
                    brain.getBot().stop();
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

    public void register(Mission<?> mission) {
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

    public void forget(Mission<?> m) {
        this.nextMission.remove(m);
    }

    public Mission<?> popMission() {
        //this.forget(this.getCurrentMission());
        if (!this.nextMission.isEmpty()) {
            return this.nextMission.remove(0);
        }
        throw new NoSuchElementException("Cannot pop an empty brain - it's dead Negan!");
    }

    public void registerImmediate(AetherTask<?> task) {
        Logging.info("Registering immediate AetherTask[" + this.immediateRoot.size() + "]: " + task.getClass().getSimpleName());
        this.immediateRoot.add(task);
    }

    private void setLastThought(String thought) {
        Logging.info("[Brain] " + thought);
        this.lastThought = thought;
    }


    private AetherTask<?> selectNextTask(Brain brain) {
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
        if (this.nextMission.isEmpty()) {
            return brain.getBot().getState() == State.UNSTARTED ? AetherTask.NOTHING : null;
        }
        Mission<?> task = this.nextMission.get(0);
        if (task.hasEnded()) {
            this.nextMission.remove(0);
            if (this.nextMission.isEmpty()) {
                if (brain.getBot().getState() == State.UNSTARTED) {
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

    public void delayUntil(CompletableFuture<Invalidator> task) {
        this.runningExecs.add(0, task);
    }

    public Mission<?> getCurrentMission() {
        return this.nextMission.isEmpty() ? null : this.nextMission.get(0);
    }

    public String getLastThought() {
        return this.lastThought;
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
}
