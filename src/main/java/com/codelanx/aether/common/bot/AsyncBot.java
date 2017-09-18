package com.codelanx.aether.common.bot;

import com.codelanx.aether.common.RunemateLoggerProxy;
import com.codelanx.aether.common.input.UserInput;
import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.rest.RestLoader;
import com.codelanx.commons.logging.Logging;
import com.codelanx.commons.util.Reflections;
import com.runemate.game.api.script.framework.AbstractBot;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AsyncBot extends AbstractBot {

    private AetherScheduler scheduler;
    private final Brain brain;
    private final AtomicBoolean stopping = new AtomicBoolean();
    private final RestLoader data;

    public AsyncBot() {
        Aether.setBot(this);
        this.scheduler = new AetherScheduler(this);
        this.data = new RestLoader(this);
        this.brain = new Brain(this);
        Logger l = new RunemateLoggerProxy(this.getLogger());
        Logging.setNab(() -> l);
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
            this.brain.loop();
        } catch (Throwable t) {
            Logging.log(Level.SEVERE, "Uncaught exception in bot loop", t);
            throw t;
        }
    }

    public Brain getBrain() {
        return this.brain;
    }

    public RestLoader getData() {
        return this.data;
    }

    @Override
    public final void onStart(String... strings) {
        super.onStart(strings);
        Logging.info("#onStart(" + Arrays.toString(strings) + ")");
        this.onBotStart(strings);
        this.scheduler.register(this);
    }


    @Override
    public final void onStop() {
        Logging.info("#onStop");
        super.onStop();
        this.stopping.set(true);
        this.scheduler.stop();
        this.brain.getLogicTree().clear();
        UserInput.wipe();
        Caches.invalidateAll();
        this.onBotStop();
    }
    //instead of being empty, we leave them as abstract to discourage autofillers from placing a supercall
    //a recurrant supercall to #onStart or similar is quite dangerous
    //thus the methods are forced to be filled in, and in many cases everything but #onBotStart may be blank
    public abstract void onBotStart(String... args);
    public abstract void onBotStop();
    public abstract void onBotPause();
    public abstract void onBotResume();

    @Override
    public final void onPause() {
        super.onPause();
        this.scheduler.pause();
        this.brain.getLogicTree().invalidate();
        UserInput.wipe();
        Caches.invalidateAll();
        this.onBotPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.onBotResume();
        this.scheduler.resume(this);
    }

    public File getResourcePath() {
        return new File("resources");
    }

    public AetherScheduler getScheduler() {
        return this.scheduler;
    }
}
