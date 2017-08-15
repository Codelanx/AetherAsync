package com.codelanx.aether.common.branch.recipe;

import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.branch.InteractionTask;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog;
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

    @Override
    public Supplier<Boolean> getStateNow() {
        Environment.getLogger().info("Range validation time");
        Environment.getLogger().info("Chat title: " + ChatDialog.getTitle());
        Environment.getLogger().info("Container loaded: " + InterfaceContainers.isLoaded(307));
        InterfaceComponentQueryResults res = Interfaces.newQuery().containers(this.recipe.getContainerId()).visible().results();
        Environment.getLogger().info("Query: " + res);
        Supplier<Boolean> back = () -> res.size() > 0;
        //this is awful
        if (this.justClicked) {
            Execution.delay(500);
            boolean oldClick = this.justClicked;
            this.justClicked = false;
            back = this.getStateNow();
            this.justClicked = oldClick;
        }
        this.justClicked = !back.get();
        return back;
    }

}

