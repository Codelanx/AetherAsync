package com.codelanx.aether.common;

import com.runemate.game.api.script.framework.tree.TreeBot;
import com.runemate.game.api.script.framework.tree.TreeTask;

public abstract class AetherBot extends TreeBot {

    private final Brain brain;
    private final CachedInventory inventory;
    private final CachedGameObjects gameObjects;
    private static AetherBot instance;

    public AetherBot() {
        this.inventory = new CachedInventory();
        this.gameObjects = new CachedGameObjects();
        this.brain = new Brain(this);
        instance = this;
        //this.setLoopDelay(1000, 2000);
        //this.setLoopDelay(500, 1000);
    }

    @Override
    public void onPause() {
        this.getInventory().invalidateAll();
    }


    @Override
    public void onStop() {
        super.onStop();
        this.getBrain().lobotomy();
        this.getInventory().invalidateAll();
    }

    public Brain getBrain() {
        return this.brain;
    }

    public CachedInventory getInventory() {
        return this.inventory;
    }

    public CachedGameObjects getGameObjects() {
        return this.gameObjects;
    }

    @Override
    public TreeTask createRootTask() {
        return this.brain;
    }

    //should only be used by commons tasks, since the field will
    //be incomplete when set (for subclasses) and subclasses can choose
    //to have their own (type-compatible) pseudo-singleton reference
    public static AetherBot get() {
        return instance;
    }

}
