package com.codelanx.aether.common.branch.async.recipe;

import com.codelanx.aether.common.bot.async.Invalidator;
import com.codelanx.aether.common.bot.async.Invalidators;
import com.codelanx.aether.common.bot.async.mouse.ClickHandler;
import com.codelanx.aether.common.recipe.Recipe;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceContainer;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceContainers;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.script.Execution;

import java.util.function.Supplier;

public class CreateTask implements Supplier<Invalidator> {

    private final Recipe recipe;

    public CreateTask(Recipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public Invalidator get() {
        InterfaceContainer cont;
        SpriteItem item;
        switch (this.recipe.getRecipeType()) {
            case COOK:
                Environment.getLogger().info("\t\t=>CreateTask");
                if (!InterfaceContainers.isLoaded(this.recipe.getContainerId())) {
                    Execution.delay(100);
                    return Invalidators.NONE;
                }
                cont = InterfaceContainers.getAt(this.recipe.getContainerId());
                cont.getComponents(i -> true).random().interact("Cook All");
                break;
            case SMELT:
                Environment.getLogger().info("\t\t=>CreateTask");
                if (!InterfaceContainers.isLoaded(this.recipe.getContainerId())) {
                    Execution.delay(100);
                    return Invalidators.NONE;
                }
                cont = InterfaceContainers.getAt(this.recipe.getContainerId());
                cont.getComponents(i -> true).random().interact("Smelt All");
                break;
            case CLICK:
                //get next item in inventory, click it
                //Validate.isTrue(this.recipe.getIngrediateCount() == 1, "Cannot have a CLICK recipe with multiple ingredients");
                item = this.recipe.getIngredientsInInventory().first();
                if (item != null) {
                    ClickHandler.click(item);
                }
                return Invalidators.NONE;
            case COMBINE: //TODO
                if (this.recipe.getToolSpace() > 0) {
                    item = this.recipe.getToolsInInventory().first();
                    //click tool on itemsSpriteItem i = this.recipe.getIngredientsInInventory().first();
                    if (item != null) {
                        SpriteItem target = this.recipe.getIngredientsInInventory().first();
                        if (target != null) {
                            ClickHandler.interact(item, "Use");
                            ClickHandler.click(target);
                            if (this.recipe.isAutomatic()) {
                                //TODO: Menu handling
                            }
                            return Invalidators.NONE;
                        }
                    }
                }
        }
        return Invalidators.ALL;
    }
}
