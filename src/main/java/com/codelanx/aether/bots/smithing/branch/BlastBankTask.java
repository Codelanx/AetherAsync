package com.codelanx.aether.bots.smithing.branch;

import com.codelanx.aether.bots.defender.branch.WithDragonDefender;
import com.codelanx.aether.bots.smithing.BlastData;
import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.branch.bank.WithdrawTask;
import com.codelanx.aether.common.json.item.ItemLoader;
import com.codelanx.aether.common.json.item.ItemStack;
import com.codelanx.aether.common.json.item.Material;
import com.codelanx.aether.common.json.recipe.Recipe;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlastBankTask extends AetherTask<Boolean> {

    private final boolean usesCoal;

    public BlastBankTask(Recipe bar) {
        ItemLoader loader = Aether.getBot().getData().getKnownItems();
        List<ItemStack> items = bar.getFullUnit().map(o -> o.getMaterial().getName().equalsIgnoreCase("Coal") ? o.setQuantity(o.getQuantity() / 2) : o)
                .filter(i -> i.getQuantity() > 0)
                .collect(Collectors.toList());
        this.usesCoal = items.stream().map(ItemStack::getMaterial).map(Material::getName).noneMatch("Coal"::equalsIgnoreCase);
        int nonCoal = items.size() - (this.usesCoal ? 1 : 0);
        if (nonCoal <= 0) {
            //invalid recipe, it's literally fucking coal
        }
        Supplier<Stream<ItemStack>> base = () -> Stream.of("Coins", "Stamina Potion (4)", "Bucket of water").map(loader::getItem).map(ItemStack::new);
        Supplier<Stream<ItemStack>> noCoal = () -> items.stream().filter(i -> "Coal".equalsIgnoreCase(i.getMaterial().getName()));
        Stream<ItemStack> coal = Stream.concat(base.get(), Stream.of(new ItemStack(loader.getItem("Coal"), BlastData.ORE_PER_TRIP)));
        Stream<ItemStack> ores;
        if (nonCoal == 1) {
            ores = noCoal.get().map(i -> i.setQuantity(BlastData.ORE_PER_TRIP));
            ores = Stream.concat(base.get(), ores);
        } else {
            //future bug if they ever have a bar which requires 2+ ores and coal
            int space = 28 / noCoal.get().map(ItemStack::getQuantity).reduce(0, Integer::sum);
            ores = noCoal.get().map(i -> i.setQuantity(i.getQuantity() * space));
        }
        this.register(false, new WithdrawTask(Stream.concat(base.get(), bar.fullInventoryWithdrawl(25)).collect(Collectors.toList())));
        this.register(true, new WithdrawTask(Stream.concat(base.get(), Stream.of()).collect(Collectors.toList())));
    }


    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> {
            return this.usesCoal && BlastData.COAL_IN_FURNACE.as(int.class) < BlastData.COAL_FILL_LIMIT;
        };
    }
}
