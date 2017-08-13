package com.codelanx.aether.common.bot.sync;

import com.codelanx.aether.common.RunnableLeaf;
import com.codelanx.aether.common.branch.sync.CommonActions;
import com.codelanx.aether.common.mission.Mission;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.AbstractBot;
import com.runemate.game.api.script.framework.AbstractBot.State;
import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.TreeTask;

import java.util.ArrayList;
import java.util.List;

//delegates taskwork
public class Brain extends BranchTask {

    private static final long START_MS = System.currentTimeMillis();
    private final List<Runnable> immediateTasks = new ArrayList<>();
    private final List<TreeTask> immediateRoot = new ArrayList<>();
    private final List<Mission> tasks = new ArrayList<>();
    private final AbstractBot bot;
    private final Neuron neuron;

    public Brain(AbstractBot bot) {
        this.bot = bot;
        this.neuron = new Neuron();
    }

    @Override
    public TreeTask successTask() {
        this.neuron.invalidate();
        return this.neuron;
    }

    private TreeTask selectNextTask() {
        Runnable immediate = this.immediateTasks.isEmpty() ? null : this.immediateTasks.remove(0);
        if (immediate != null) {
            Environment.getLogger().info("[Brain] Running immediate task...");
            return RunnableLeaf.of(immediate);
        }
        TreeTask immediateRoot = this.immediateRoot.isEmpty() ? null : this.immediateRoot.remove(0);
        if (immediateRoot != null) {
            Environment.getLogger().info("[Brian] Running immediate TreeTask (" + immediateRoot.getClass().getSimpleName() + ")...");
            return immediateRoot;
        }
        Mission task = this.tasks.get(0);
        if (task.hasEnded()) {
            this.tasks.remove(0);
            if (this.tasks.isEmpty()) {
                return this.failureTask();
            }
            task = this.tasks.get(0);
        }
        Environment.getLogger().info("[" + (System.currentTimeMillis() - START_MS) + "] Running mission: " + task.getClass().getSimpleName());
        return task;
    }

    public Mission getCurrentMission() {
        return this.tasks.isEmpty() ? null : this.tasks.get(0);
    }

    @Override
    public TreeTask failureTask() {
        if (this.bot.getState() == State.UNSTARTED) {
            return CommonActions.WAIT.getTask();
        }
        return CommonActions.END.getTask();
    }

    public void lobotomy() {
        this.tasks.clear();
        this.immediateRoot.clear();
        this.immediateTasks.clear();
    }

    @Override
    public boolean validate() {
        return this.immediateTasks.size() > 0 || this.tasks.size() > 0 || this.immediateRoot.size() > 0;
    }

    public void register(Mission mission) {
        this.tasks.add(mission);
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

    public void forget(Mission m) {
        this.tasks.remove(m);
    }

    public void popMission() {
        //this.forget(this.getCurrentMission());
        if (!this.tasks.isEmpty()) {
            this.tasks.remove(0);
        }
    }

    public void registerImmediate(TreeTask task) {
        Environment.getLogger().info("Registering immediate TreeTask[" + this.immediateRoot.size() + "]: " + task.getClass().getSimpleName());
        this.immediateRoot.add(task);
    }

    //because #successTask/#failureTask are called twice, but invoked once, we'll use this for smart scheduling
    public class Neuron extends TreeTask {

        private TreeTask currentRoot = null;

        public void invalidate() {
            this.currentRoot = null;
        }

        private void fulfillRoot() {
            if (this.currentRoot != null) {
                return;
            }
            this.currentRoot = Brain.this.selectNextTask();
        }

        @Override
        public boolean validate() {
            this.fulfillRoot();
            return this.currentRoot.validate();
        }

        @Override
        public TreeTask failureTask() {
            this.fulfillRoot();
            return this.currentRoot.failureTask();
        }

        @Override
        public void execute() {
            this.fulfillRoot();
            this.currentRoot.execute();
        }

        @Override
        public TreeTask successTask() {
            this.fulfillRoot();
            return this.currentRoot.successTask();
        }

        @Override
        public boolean isLeaf() {
            this.fulfillRoot();
            return this.currentRoot.isLeaf();
        }
    }
}
