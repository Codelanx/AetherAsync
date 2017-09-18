package com.codelanx.aether.common.branch.bank;

import com.codelanx.aether.common.Common.Banks;
import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.Common;
import com.codelanx.aether.common.bot.Invalidator;
import com.codelanx.aether.common.bot.Invalidators;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.input.UserInput;
import com.codelanx.aether.common.json.item.ItemStack;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.codelanx.commons.logging.Logging;
import com.codelanx.commons.util.RNG;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//a bit of a mutative task. It can either handle a simple direct-withdrawl of an item, or full-scale withdrawl of a recipe
//TODO: Need to support a non-perfect inventory (one that wasn't managed externally)
public class WithdrawTask extends AetherTask<Map<ItemStack, List<SpriteItem>>> {

    private static final Map<ItemStack, List<SpriteItem>> DEPOSIT_ALL = new HashMap<>(); //should never be added to / read, mostly used for comparison
    private final Set<ItemStack> reusable = new HashSet<>();
    private final List<ItemStack> withdrawl = new ArrayList<>();
    private final Map<Integer, List<ItemStack>> grouped;

    static {
        DEPOSIT_ALL.put(null, Collections.emptyList());
    }

    public WithdrawTask(Recipe recipe) {
        this(supplies(recipe), (AetherTask<?>) null);
        this.registerDefault(() -> {
            //hm, we could use the state here too
            return Invalidators.ALL;
        });
        /*this(false, () -> {
            //TODO: smarter edge cases
            if (recipe.getIngredients().map(ItemStack::getMaterial).allMatch(Material::isStackable)) {

            }
            Stream<ItemStack> str;
            if (recipe.getIngredientCount() > 1 || recipe.usesTools()) {
                //withdraw specific amounts in groups
                Map<Integer, List<ItemStack>> groupedWithdraw = recipe.fullInventoryWithdrawl()
                        .collect(Collectors.groupingBy(ItemStack::getQuantity));
                str = groupedWithdraw.values().stream().flatMap(Collection::stream);
            } else {
                str = recipe.fullInventoryWithdrawl();
            }
            str.filter(Common.Banks::withdrawItem).findAny().ifPresent(i -> {
                Logging.info("No more " + i.getMaterial().getName() + " available, unregistering mission...");
                Aether.getBot().getBrain().getLogicTree().popMission();
            });
            return Invalidators.ALL;*/
    }

    public WithdrawTask(ItemStack... items) {
        this(Arrays.asList(items));
    }

    public WithdrawTask(List<ItemStack> items) {
        this(items, AetherTask.ofRunemateFailable(() -> items.stream().noneMatch(Common.Banks::withdrawItem)));
    }

    private WithdrawTask(List<ItemStack> items, AetherTask<?> onWithdrawl) {
        this(items, null, onWithdrawl);
    }

    //note the implication here, we won't be able to determine if an inventory should be fully deposited or not
    //we can't even guesswork for ingredients, as #reusable is unsettable.
    private WithdrawTask(List<ItemStack> items, Consumer<AetherTask<Map<ItemStack, List<SpriteItem>>>> onConstruct, AetherTask<?> onWithdrawl) {
        this.grouped = items.stream().collect(Collectors.groupingBy(ItemStack::getQuantity,
                () -> new TreeMap<>((i1, i2) -> i1 == 1 ? 1 : i2 == 1 ? -1 : 0), //we ensure that single-withdrawls are first. After that I don't care. Replace 0 with a comparison if you like I guess
                Collectors.toList()));
        this.grouped.values().removeIf(Collection::isEmpty);
        this.register(Map::isEmpty, () -> {
            //TODO: Proper withdrawl
            this.grouped.forEach((amt, itms) -> {
                itms.forEach(i -> UserInput.runemateInput(() -> Banks.withdrawItem(i))); //TODO Utilize user input
            });
        }); //empty map == no deposits, we can withdraw
        this.registerDefault(state -> {
            if (state == DEPOSIT_ALL //we do a double-check for DEPOSIT_ALL, for sanity's sake
                    || state.containsKey(null) && Optional.ofNullable(state.get(null)).map(Collection::isEmpty).orElse(false)) {
                return Common.Banks.depositInventory() ? Invalidators.ALL : Invalidators.NONE;
            }
            state.forEach((stack, list) -> {
                SpriteItem item = RNG.get(state.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
                UserInput.interact(item, "Deposit-All").postAttempt().thenRun(() -> Caches.forInventory().invalidateByType(stack.getMaterial()));
            });
            return Invalidators.ALL;
        });
        //this.registerInvalidator(false, onWithdrawl);
        //this.registerRunemateCall(true, Common.Banks::depositInventory);

    }

    private static List<ItemStack> supplies(Recipe recipe) {
        Stream<ItemStack> str;
        if (recipe.getIngredientCount() > 1 || recipe.usesTools()) {
            //withdraw specific amounts in groups
            //this is solely for changing order, maybe a sort would be better
            Map<Integer, List<ItemStack>> groupedWithdraw = recipe.fullInventoryWithdrawl()
                    .collect(Collectors.groupingBy(ItemStack::getQuantity));
            str = groupedWithdraw.values().stream().flatMap(Collection::stream);
        } else {
            str = recipe.fullInventoryWithdrawl();
        }
        return str.collect(Collectors.toList());
    }

    private <T> Function<T, T> wrap(Consumer<T> exec) {
        return o -> {
            exec.accept(o);
            return o;
        };
    }

    private enum WithdrawState {
        DEPOSIT_ALL,
        DEPOSIT_PARTIAL,
        WITHDRAW;
    }

    @Override
    public Supplier<Map<ItemStack, List<SpriteItem>>> getStateNow() {
        return () -> {
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
        };
    }
}
