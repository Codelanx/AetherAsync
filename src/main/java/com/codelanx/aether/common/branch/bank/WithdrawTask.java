package com.codelanx.aether.common.branch.bank;

import com.codelanx.aether.common.AetherBot;
import com.codelanx.aether.common.Common;
import com.codelanx.aether.common.RunnableLeaf;
import com.codelanx.aether.common.item.ItemStack;
import com.codelanx.aether.common.recipe.Recipe;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.TreeTask;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WithdrawTask extends BranchTask {

    private final Recipe recipe;
    private final TreeTask success;
    private final TreeTask failure;
    private int lastWithdrewAmount = -1;

    public WithdrawTask(Recipe recipe) {
        this.recipe = recipe;
        this.success = RunnableLeaf.of(() -> {
            //TODO: smarter edge cases
            Stream<ItemStack> str;
            if (recipe.getIngrediateCount() > 1 || recipe.usesTools()) {
                //withdraw specific amounts in groups
                Map<Integer, List<ItemStack>> groupedWithdraw = recipe.fullInventoryWithdrawl()
                        .collect(Collectors.groupingBy(ItemStack::getQuantity));
                str = groupedWithdraw.values().stream().flatMap(Collection::stream);
            } else {
                str = recipe.getIngredients();
            }
            str.filter(Common.Banks::withdrawItem).findAny().ifPresent(i -> {
                Environment.getLogger().info("No more " + i.getMaterial().getName() + " available, unregistering mission...");
                AetherBot.get().getBrain().popMission();
            });
        });
        this.failure = RunnableLeaf.of(Common.Banks::depositInventory);
    }

    @Override
    public TreeTask successTask() {
        return this.success;
    }

    @Override
    public boolean validate() {
        return Inventory.newQuery().results().size() <= 0;
    }

    @Override
    public TreeTask failureTask() {
        return this.failure;
    }
}
