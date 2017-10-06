package com.codelanx.aether.common.branch.bank;

import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.json.item.ItemStack;
import com.codelanx.aether.common.json.item.Material;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class WithdrawTask2 extends AetherTask<Boolean> {

    //private final Map<Material, ItemStack> stacks; //TODO
    //private final Map<Integer, List<ItemStack>> grouped; //TODO

    public WithdrawTask2(Recipe recipe) {

    }

    public WithdrawTask2(List<ItemStack> items) {

    }


    @Override
    public Supplier<Boolean> getStateNow() {
        /*return () -> {
            //We'll do a comparison of current inventory to expected, and based on differences
            Map<Material, Integer> comp = Caches.forInventory().getReducedItems();
            Map<ItemStack, Integer> needed = Collections.emptyMap();

            this.stacks.entrySet().stream().map(ent -> {
                int need = ent.getValue().getQuantity();
                int have = comp.getOrDefault(ent.getKey(), 0);
                return need - have;
            });

            //we'll break it down into how many left/right clicks are needed
            comp.entrySet().stream().collect(Collectors.partitioningBy(ent -> ent.getValue() > 1)); //>1 == right click

                    .map(ent -> {
                int curr = ent.getValue();
                int need = Optional.ofNullable(this.stacks.get(ent.getKey())).map(ItemStack::getQuantity).orElse(0);

            });




            if (this.reusable.isEmpty() || this.reusable.size() < this.withdrawl.size()) { //TODO: variance of discretion here
                return !Caches.forInventory().isEmpty() ? DEPOSIT_ALL : Collections.emptyMap();
            }
            //calculate based on consumed/non-reusable items
            List<ItemStack> consumed = this.withdrawl.stream().filter(this.reusable::contains).collect(Collectors.toList());
            //an expensive operation
            Map<ItemStack, List<SpriteItem>> rawInventory = consumed.stream().collect(Collectors.toMap(Function.identity(), i -> {
                return Caches.forInventory().getResults(i.getMaterial()).get().asList();
            }));
            rawInventory.values().removeIf(Collection::isEmpty); //remove already deposited items
            if (rawInventory.isEmpty()) { //well, empty enough anyhow
                return Collections.emptyMap();
            }
            return rawInventory;
        };*/
        return null; //TODO: fuck me
    }

    public static WithdrawTask2 forRecipe(Recipe recipe, boolean depositFirst) {
        return null;
    }
}
