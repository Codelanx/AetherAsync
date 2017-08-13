package com.codelanx.aether.common.branch.async;

import com.codelanx.aether.common.bot.async.Invalidator;
import com.codelanx.aether.common.bot.async.Invalidators;
import com.codelanx.aether.common.bot.sync.AetherBot;
import com.codelanx.aether.common.branch.sync.CommonActions;
import com.codelanx.aether.common.branch.sync.recipe.type.GoToRangeTask;
import com.codelanx.aether.common.item.ItemStack;
import com.codelanx.aether.common.recipe.Recipe;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.collections.Pair;
import com.runemate.game.api.script.Execution;

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
            CommonActions.END.getTask().execute();
            return Invalidators.NONE;
        }
        SpriteItemQueryResults res = Inventory.newQuery().names(ritem.getMaterial().getName()).equipable(false).stacks(ritem.getMaterial().isStackable()).ids(ritem.getId()).results();
        if (res.isEmpty()) {
            Environment.getLogger().info("Attempted to click a recipe ingredient, but they're all gone. Returning to bank");
            AetherBot.get().getInventory().set(ritem.getMaterial(), 0);
            return Invalidators.NONE;
        }
        Pair<Integer, Integer> delay = AetherBot.get().getLoopDelay();
        if (res.random().click()) {
            Execution.delay(delay.getLeft(), delay.getRight());
        } else {
            return Invalidators.NONE;
        }
        GameObject obj = GoToRangeTask.findRange();
        for (int attempts = 0; obj != null && attempts < 5; attempts++) {
            Execution.delay(delay.getLeft(), delay.getRight());
            obj = GoToRangeTask.findRange();
        }
        if (obj == null) {
            Environment.getLogger().info("Error: Cannot locate target at time of interaction");
            CommonActions.END.getTask().execute();
            return Invalidators.ALL;
        }
        if (obj.interact("Use")) {
            Environment.getLogger().info("Range used");
            //exec us immediately, dirty hack/goto: BuildSelectorLeaf
            //TreeTask target = BasicBitchBot.get().getBrain().getCurrentMission().getRoot().successTask();
            //BasicBitchBot.get().getBrain().registerImmediate(target);
        }
        return Invalidators.ALL;
    }
}
