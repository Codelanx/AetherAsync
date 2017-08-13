package com.codelanx.aether.common.branch.sync.recipe;

import com.codelanx.aether.common.branch.sync.InteractionTask;
import com.codelanx.aether.common.recipe.Recipe;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceContainers;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.hybrid.queries.results.InterfaceComponentQueryResults;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.TreeTask;

public class TargetSelectTask extends BranchTask {

    private final Recipe recipe;
    private final TreeTask success;
    private final TreeTask failure;
    private boolean justClicked = false;

    public TargetSelectTask(Recipe recipe) {
        this.recipe = recipe;
        this.success = new CreateTask(recipe);
        this.failure = new InteractionTask(recipe);
    }

    @Override
    public TreeTask successTask() {
        return this.success;
    }

    @Override
    public boolean validate() {
        Environment.getLogger().info("Range validation time");
        Environment.getLogger().info("Chat title: " + ChatDialog.getTitle());
        Environment.getLogger().info("Container loaded: " + InterfaceContainers.isLoaded(307));
        InterfaceComponentQueryResults res = Interfaces.newQuery().containers(this.recipe.getContainerId()).visible().results();
        Environment.getLogger().info("Query: " + res);
        boolean back = res.size() > 0;
        //this is awful
        if (this.justClicked) {
            Execution.delay(500);
            boolean oldClick = this.justClicked;
            this.justClicked = false;
            back = this.validate();
            this.justClicked = oldClick;
        }
        this.justClicked = !back;
        return back;
    }

    @Override
    public TreeTask failureTask() {
        return this.failure;
    }
}

