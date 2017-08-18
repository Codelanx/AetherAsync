package com.codelanx.aether.common.branch;

import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.Invalidator;
import com.codelanx.aether.common.bot.Invalidators;
import com.codelanx.aether.common.input.UserInput;
import com.codelanx.aether.common.branch.recipe.type.GoToRangeTask;
import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.json.item.ItemStack;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.region.Players;

import java.util.function.Supplier;

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
        UserInput.click(item).postAttempt().thenRunAsync(() -> {
            //Locate range, then click it
            GameObject range = GoToRangeTask.findRange();
            if (range == null) {
                //well fuck
                Environment.getLogger().info("Error: Cannot locate target at time of interaction");
                Aether.getBot().stop();
            }
            UserInput.interact(range, "Use");
        });
        return Invalidators.ALL;
    }
}
