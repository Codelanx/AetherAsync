package com.codelanx.aether.common.bot;

import com.codelanx.aether.common.RunemateLoggerProxy;
import com.codelanx.aether.common.input.UserInput;
import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.json.item.ItemLoader;
import com.codelanx.aether.common.json.recipe.RecipeLoader;
import com.codelanx.commons.logging.Logging;
import com.codelanx.commons.util.Reflections;
import com.runemate.game.api.script.framework.AbstractBot;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public abstract class AsyncBot extends AbstractBot {

    private AetherScheduler scheduler;
    private final Brain brain;
    private final ItemLoader items;
    private final RecipeLoader recipes;
    private final AtomicBoolean stopping = new AtomicBoolean();

    public AsyncBot() {
        Aether.setBot(this);
        this.scheduler = new AetherScheduler(this);
        this.items = new ItemLoader(this);
        this.recipes = new RecipeLoader(this);
        this.brain = new Brain(this);
    }

    @Override
    public final void run() {
        Logging.info("#run");
        while (!this.scheduler.isShutdown()) {
            if (this.stopping.get()) {
                this.scheduler.stop();
            }
            if (!this.isPaused()) {
                Logging.fine("Lazily observed thread pool count: " + this.scheduler.getThreadPool().getActiveCount());
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Logging.info("Exception while sleeping provided bot thread:");
                Logging.info(Reflections.stackTraceToString(e));
            }
        }
    }

    public void loop() {
        try {
            Logging.info("[Bot] Running brain loop...");
            this.brain.loop();
        } catch (Throwable t) {
            Logging.log(Level.SEVERE, "Uncaught exception in bot loop", t);
            throw t;
        }
    }

    public Brain getBrain() {
        return this.brain;
    }

    @Override
    public void onStart(String... strings) {
        super.onStart(strings);
        Logging.setNab(() -> new RunemateLoggerProxy(this.getLogger()));
        Logging.info("#onStart(" + Arrays.toString(strings) + ")");
        this.scheduler.register(this);
    }

    @Override
    public void onStop() {
        Logging.info("#onStop");
        super.onStop();
        this.stopping.set(true);
        this.brain.getLogicTree().clear();
        UserInput.wipe();
        Caches.invalidateAll();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.scheduler.pause();
        this.brain.getLogicTree().invalidate();
        UserInput.wipe();
        Caches.invalidateAll();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.scheduler.resume(this);
    }

    public File getResourcePath() {
        return new File("resources");
    }

    public AetherScheduler getScheduler() {
        return this.scheduler;
    }

    public RecipeLoader getRecipes() {
        return this.recipes;
    }

    public ItemLoader getKnownItems() {
        return this.items;
    }
}
