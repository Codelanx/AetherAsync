package com.codelanx.aether.common.branch.async.bank;

import com.codelanx.aether.common.bot.sync.AetherBot;
import com.codelanx.aether.common.Common;
import com.codelanx.aether.common.bot.async.task.AetherTask;
import com.codelanx.aether.common.item.ItemStack;
import com.codelanx.aether.common.recipe.Recipe;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WithdrawTask extends AetherTask<Boolean> {

    private final Recipe recipe;
    private int lastWithdrewAmount = -1;

    public WithdrawTask(Recipe recipe) {
        this.recipe = recipe;
        this.registerRunemateCall(true, () -> {
            //TODO: smarter edge cases
            Stream<ItemStack> str;
            if (recipe.getIngredientCount() > 1 || recipe.usesTools()) {
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
            return false;
        });
        this.registerRunemateCall(false, Common.Banks::depositInventory);
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> Inventory.newQuery().results().size() <= 0;
    }
}
