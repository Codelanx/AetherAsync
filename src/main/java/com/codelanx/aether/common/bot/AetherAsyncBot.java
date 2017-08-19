package com.codelanx.aether.common.bot;

import com.codelanx.aether.common.input.UserInput;
import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.input.UserInputException;
import com.codelanx.aether.common.json.item.ItemLoader;
import com.codelanx.aether.common.json.recipe.RecipeLoader;
import com.codelanx.commons.util.Reflections;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.script.framework.AbstractBot;
import com.runemate.game.api.script.framework.task.Task;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AetherAsyncBot extends AbstractBot {

    private AetherScheduler scheduler;
    private final AetherBrain brain;
    private final ItemLoader items;
    private final RecipeLoader recipes;
    private final AtomicBoolean stopping = new AtomicBoolean();

    public AetherAsyncBot() {
        Aether.setBot(this);
        this.scheduler = new AetherScheduler(this);
        this.items = new ItemLoader(this);
        this.recipes = new RecipeLoader(this);

        this.brain = new AetherBrain(this);
    }

    @Override
    public final void run() {
        Environment.getLogger().info("#run");
        while (!this.scheduler.isShutdown()) {
            if (this.stopping.get()) {
                this.scheduler.stop();
            }
            if (!this.isPaused()) {
                Environment.getLogger().debug("Lazily observed thread pool count: " + this.scheduler.getThreadPool().getActiveCount());
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Environment.getLogger().info("Exception while sleeping provided bot thread:");
                Environment.getLogger().info(Reflections.stackTraceToString(e));
            }
        }
    }

    private final AtomicBoolean handlingGameEvent = new AtomicBoolean();

    public void loop() {
        //handle events here I suppose
        try {
            Task t = this.getGameEventController();
            if (t != null && !this.handlingGameEvent.get()) {
                this.handlingGameEvent.set(true);
                Environment.getLogger().info("[Bot] Registering game event task...");
                Environment.getLogger().info(t.getClass().getName());
                Environment.getLogger().info(t.getChildren());
                Environment.getLogger().info(t.getParent());
                this.brain.registerImmediate(() -> {
                    if (t.validate()) {
                        this.brain.registerImmediate(() -> {
                            t.execute();
                            this.handlingGameEvent.set(false);
                        });
                    }
                });
            } else if (UserInput.hasTasks()) {
                try {
                    UserInput.attempt();
                } catch (UserInputException ex) {
                    Environment.getLogger().info("Error while attempting user input, invalidating bot and retrying...");
                    this.brain.stroke();
                    UserInput.wipe();
                    Caches.invalidateAll();
                }
                //Environment.getLogger().info("[Bot] Input handler has tasks, returning...");
                return;
            }
            if (this.brain.isThinking()) {
                //Environment.getLogger().info("[Bot] Brain thinking, resting bot thread...");
                return;
            }
            Environment.getLogger().info("[Bot] Running brain loop...");
            this.brain.loop();
        } catch (Throwable t) {
            Environment.getLogger().info("Uncaught exception in bot loop");
            Environment.getLogger().info(Reflections.stackTraceToString(t));
            throw t;
        }
    }

    public AetherBrain getBrain() {
        return this.brain;
    }

    @Override
    public void onStart(String... strings) {
        super.onStart(strings);
        Environment.getLogger().info("#onStart(" + Arrays.toString(strings) + ")");
        this.scheduler.register(this);
    }

    @Override
    public void onStop() {
        Environment.getLogger().info("#onStop");
        super.onStop();
        this.stopping.set(true);
        this.brain.lobotomy();
        UserInput.wipe();
        Caches.invalidateAll();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.scheduler.pause();
        this.brain.stroke();
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
