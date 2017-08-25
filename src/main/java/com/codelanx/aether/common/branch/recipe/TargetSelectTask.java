package com.codelanx.aether.common.branch.recipe;

import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.codelanx.aether.common.menu.dialog.DialogueIterator;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceContainers;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.hybrid.queries.results.InterfaceComponentQueryResults;
import com.runemate.game.api.script.Execution;

import java.util.function.Supplier;

public class TargetSelectTask extends AetherTask<Boolean> {

    private final Recipe recipe;
    private boolean justClicked = false;

    public TargetSelectTask(Recipe recipe) {
        this.recipe = recipe;
        this.registerInvalidator(true, new CreateTask(recipe));
        this.registerInvalidator(false, new InteractionTask(recipe));
    }

    /*@Override
    public boolean isSync() {
        return !this.recipe.isAutomatic();
    }*/

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> {
            //TODO: Optimize
            Environment.getLogger().info("Recipe target validation time");
            Environment.getLogger().info("Chat title: " + DialogueIterator.getTitleSafe());
            Environment.getLogger().info("Container loaded: " + InterfaceContainers.isLoaded(this.recipe.getContainerId()));
            InterfaceComponentQueryResults res = Interfaces.newQuery().containers(this.recipe.getContainerId()).visible().results();
            Environment.getLogger().info("Query: " + res);
            return res.size() > 0;
        };
    }

}

