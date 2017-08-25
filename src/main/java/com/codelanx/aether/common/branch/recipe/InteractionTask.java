package com.codelanx.aether.common.branch.recipe;

import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.AsyncExec;
import com.codelanx.aether.common.bot.Invalidator;
import com.codelanx.aether.common.bot.Invalidators;
import com.codelanx.aether.common.branch.recipe.RecipeExecTask;
import com.codelanx.aether.common.input.UserInput;
import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.json.item.ItemStack;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.region.Players;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

//TODO: Abstraction around range/furnace/etc
public class InteractionTask implements Supplier<Invalidator> {

    private final Recipe recipe;

    public InteractionTask(Recipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public Invalidator get() {
        if (Players.getLocal().getAnimationId() != -1) {
            return Invalidators.NONE;
        }
        switch (this.recipe.getRecipeType()) {
            case COOK:
                ItemStack ritem = this.recipe.getIngredients().findAny().orElse(null);
                if (ritem == null) {
                    Environment.getLogger().info("Error determining recipe ingredient to use on target");
                    //CommonTasks.END.asTask().registerImmediate(); //TODO: uncomment
                    return Invalidators.NONE;
                }
                SpriteItem item = Caches.forInventory().get(ritem.getMaterial().toInquiry()).findAny().orElse(null);
                if (item == null) {
                    Environment.getLogger().info("Attempted to click a recipe ingredient, but they're all gone. Returning to bank");
                    return Invalidators.NONE;
                }
                UserInput.click(item).postAttempt().thenRun(() -> {
                    //Locate range, then click it
                    GameObject range = RecipeExecTask.findRange();
                    if (range == null) {
                        //well fuck
                        Environment.getLogger().info("Error: Cannot locate target at time of interaction");
                        Aether.getBot().stop();
                    }
                    UserInput.interact(range, "Use");
                    AsyncExec.delayUntil(() -> {
                        return Interfaces.newQuery().containers(this.recipe.getContainerId()).visible().results().size() > 0;
                    });
                });
                return Invalidators.ALL;
            case CLICK:
                //TODO: invalidate on completion, not failure
                return this.recipe.getIngredientsInInventory().findAny().map(UserInput::click).isPresent()
                        ? Invalidators.NONE
                        : Invalidators.ALL;
            case COMBINE:
                if (this.recipe.getToolSpace() > 0) {
                    item = this.recipe.getToolsInInventory().findAny().orElse(null);
                    //click tool on itemsSpriteItem i = this.recipe.getIngredientsInInventory().first();
                    if (item != null) {
                        SpriteItem target = this.recipe.getIngredientsInInventory().findAny().orElse(null);
                        if (target != null) {
                            Environment.getLogger().info("queueing input (tool)");
                            UserInput.interact(item, "Use");
                            UserInput.click(target);
                        } else {
                            Environment.getLogger().warn("null selection target");
                        }
                    } else {
                        Environment.getLogger().warn("null tool");
                    }
                    break;
                } else if (this.recipe.getIngredientCount() > 1) {
                    List<SpriteItem> items = this.recipe.getIngredientsInInventory().collect(Collectors.toList());
                    if (items.size() <= 1) {
                        //bank failure
                        Environment.getLogger().warn("only 1 (or less) items found for recipe");
                        return Invalidators.ALL;
                    }
                    Environment.getLogger().info("queueing input (mix)");
                    UserInput.click(items.get(0));
                    UserInput.click(items.get(1)).postAttempt().thenRun(() -> {
                        AsyncExec.delayUntil(() -> {
                            return Interfaces.newQuery().containers(this.recipe.getContainerId()).visible().results().size() > 0;
                        });
                    });
                    return Invalidators.ALL;
                    //return this.recipe.isAutomatic() ? Invalidators.ALL : Invalidators.NONE;
                }
                throw new RuntimeException("Bad combination recipe - not enough ingredients: " + this.recipe);
        }
        return Invalidators.ALL;
    }
}
