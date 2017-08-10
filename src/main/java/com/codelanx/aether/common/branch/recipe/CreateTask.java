package com.codelanx.aether.common.branch.recipe;

import com.codelanx.aether.common.recipe.Recipe;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceContainer;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceContainers;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.tree.LeafTask;
import org.apache.commons.lang3.Validate;

public class CreateTask extends LeafTask {

    private final Recipe recipe;

    public CreateTask(Recipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void execute() {
        InterfaceContainer cont;
        switch (this.recipe.getRecipeType()) {
            case COOK:
                Environment.getLogger().info("\t\t=>CreateTask");
                if (!InterfaceContainers.isLoaded(this.recipe.getContainerId())) {
                    Execution.delay(100);
                    return;
                }
                cont = InterfaceContainers.getAt(this.recipe.getContainerId());
                //TODO: Abstract
                cont.getComponents(i -> true).random().interact("Cook All");
                break;
            case SMELT:
                Environment.getLogger().info("\t\t=>CreateTask");
                if (!InterfaceContainers.isLoaded(this.recipe.getContainerId())) {
                    Execution.delay(100);
                    return;
                }
                cont = InterfaceContainers.getAt(this.recipe.getContainerId());
                //TODO: Abstract
                cont.getComponents(i -> true).random().interact("Smelt All");
                break;
            case CLICK:
                //get next item in inventory, click it
                //Validate.isTrue(this.recipe.getIngrediateCount() == 1, "Cannot have a CLICK recipe with multiple ingredients");
                SpriteItem i = this.recipe.getIngredientsInInventory().first();
                if (i != null) {
                    i.click();
                }
                break;
            case COMBINE: //TODO
                break;
        }
    }
}
