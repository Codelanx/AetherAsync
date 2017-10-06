package com.codelanx.aether.bots.smithing.branch;

import com.codelanx.aether.bots.smithing.BlastData;
import com.codelanx.aether.common.Filters;
import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.branch.bank.withdraw.BankRecipeTask;
import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.json.item.ItemStack;
import com.codelanx.aether.common.json.item.Material;
import com.codelanx.aether.common.json.recipe.Recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.codelanx.aether.bots.smithing.BlastData.ORE_PER_TRIP;

public class BlastBankTask extends BankRecipeTask {

    private static final ItemStack COAL = new ItemStack(Aether.getBot().getData().getItem("Coal"), ORE_PER_TRIP);
    private static final List<ItemStack> BF_TOOLS;
    private final AtomicInteger lastOre = new AtomicInteger(-1);
    private final List<ItemStack> ores = new ArrayList<>();
    private final int coalReq;

    static {
        Map<String, Integer> bfTools = new LinkedHashMap<>();
        bfTools.put("Coins", Integer.MAX_VALUE);
        bfTools.put("Stamina Potion (4)", 1);
        bfTools.put("Bucket of water", 1);
        BF_TOOLS = bfTools.entrySet().stream().map(e -> new ItemStack(Aether.getBot().getData().getItem(e.getKey()), e.getValue())).collect(Collectors.toList());
    }

    public BlastBankTask(Recipe bar) {
        //let the tools for the recipe be handled normally
        super(furnaceTools(bar));
        //manual ore handling from here
        this.coalReq = bar.getIngredients()
                .filter(Filters.of(ItemStack::getMaterial, COAL.getMaterial()::equals))
                .findAny().map(ItemStack::getQuantity).orElse(0);
        //map each required individual ore to a stack of 25, equated to 25 ores per run based on type
        bar.getIngredients().flatMap(item -> {
            List<ItemStack> back = new ArrayList<>(item.getQuantity());
            for (int i = 0; i < item.getQuantity(); i++) {
                back.add(new ItemStack(item.getMaterial(), ORE_PER_TRIP));
            }
            return back.stream();
        }).forEach(this.ores::add);
        //sort coal to the front
        Collections.sort(this.ores, (i1, i2) -> {
            boolean c1 = i1.getMaterial().getName().equalsIgnoreCase("coal");
            boolean c2 = i2.getMaterial().getName().equalsIgnoreCase("coal");
            if (c1 ^ c2) {
                return c1 ? -1 : 1; //move coal to front
            }
            return 0;
        });
    }

    //ores == true: only ores, ores == false: only tools
    private static Recipe furnaceTools(Recipe recipe) {
        if (recipe.getTools().count() > 0) {
            throw new IllegalArgumentException("Cannot smith a bar recipe with pre-defined tools");
        }
        return recipe.setIngredients(Collections.emptyList()).setTools(BF_TOOLS);
    }


    @Override
    public Supplier<ItemStack> getStateNow() {
        return () -> {
            ItemStack back = super.getStateNow().get();
            if (back != null) {
                return back;
            }
            if (Caches.forInventory().getAll().count() < 28) { //non-full inventory
                //ore handling
                int last = this.lastOre.get();
                while (back == null) {
                    last++;
                    if (last >= this.ores.size()) {
                        if (this.ores.isEmpty()) {
                            throw new IllegalStateException("No ores for blast furnace bar recipe");
                        }
                        last = 0;
                        BlastData.BARS_IN_FURNACE.set(BlastData.BARS_IN_FURNACE.as(int.class) + BlastData.ORE_PER_TRIP);
                        BlastData.COAL_IN_FURNACE.set(Math.max(BlastData.COAL_IN_FURNACE.as(int.class) - (BlastData.ORE_PER_TRIP * this.coalReq), 0));
                    }
                    back = this.ores.get(last);
                    if (this.isCoal(back)) {
                        //do we even need coal brah?
                        if (BlastData.COAL_IN_FURNACE.as(int.class) / BlastData.ORE_PER_TRIP >= this.coalReq) {
                            back = null;
                        } else {
                            BlastData.CARRYING_COAL.set(true);
                        }
                    }
                }
                this.lastOre.set(last);
                return back;
            }
            return null;
        };
    }

    private boolean isCoal(ItemStack stack) {
        return COAL.getMaterial().equals(stack.getMaterial());
    }
}
