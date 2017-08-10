package com.codelanx.aether.construction.mission.branch;

import com.codelanx.aether.construction.BasicBitchBot;
import com.codelanx.commons.util.Reflections;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.script.framework.tree.LeafTask;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TestTask extends LeafTask {

    private final ScheduledExecutorService service;
    private volatile CompletableFuture<Optional<SpriteItem>> findPlank;

    public TestTask() {
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        this.service = Executors.newSingleThreadScheduledExecutor(r -> {
            return new Thread(tg, r);
        });
    }

    @Override
    public void execute() {
        if (this.findPlank == null) {
            CompletableFuture<Optional<SpriteItem>> finder = CompletableFuture.supplyAsync(() -> {
                SpriteItemQueryResults res = Inventory.newQuery().names("Oak plank").results();
                return Optional.ofNullable(res.first());
            }, this.service);
            if (this.findPlank == null) {
                this.findPlank = finder;
            }
        }
        if (this.findPlank != null
                && (this.findPlank.isDone() || this.findPlank.isCompletedExceptionally())) {
            try {
                Optional<SpriteItem> item = this.findPlank.get();
                Environment.getLogger().info("Item value: " + item.orElse(null));
            } catch (InterruptedException | ExecutionException ex) {
                Environment.getLogger().info("Error retrieving item asynchronously: " + Reflections.stackTraceToString(ex));
            }
        } else {
            //dirty hack to re-register ourselves repeatedly until finished
            BasicBitchBot.get().getBrain().registerImmediate(this);
        }
    }
}
