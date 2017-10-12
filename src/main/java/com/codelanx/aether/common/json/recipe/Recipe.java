package com.codelanx.aether.common.json.recipe;

import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.cache.form.ContainerCache;
import com.codelanx.aether.common.cache.form.container.FixedSizeItemCache;
import com.codelanx.aether.common.cache.form.container.ItemCache;
import com.codelanx.aether.common.json.Withdrawable;
import com.codelanx.aether.common.json.item.ItemStack;
import com.codelanx.commons.logging.Logging;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceComponent;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceContainer;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceContainers;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public interface Recipe extends Withdrawable {

    //gets the name of the RECIPE, not relevant for in-game details
    public String getName();

    public Stream<ItemStack> getIngredients();

    /**
     * The total number of unique materials in a single recipe iteration
     *
     * @return The number of itemstacks by material type
     */
    public int getIngredientCount();

    /**
     * Returns any required side-tools for the recipe (e.g. a knife or a bracelet mould)
     *
     * @return
     */
    public Stream<ItemStack> getTools();

    public Stream<ItemStack> getOutput();

    public List<Integer> componentInquiry();

    default public InterfaceComponent queryInputComponent() {
        List<Integer> ints = this.componentInquiry();
        if (ints.isEmpty()) {
            return null;
        }
        Iterator<Integer> itr = ints.iterator();
        InterfaceContainer cont = InterfaceContainers.getAt(itr.next());
        if (cont == null || !itr.hasNext()) {
            return null;
        }
        InterfaceComponent next = cont.getComponent(itr.next());
        while (itr.hasNext()) {
            InterfaceComponent n = next.getComponent(itr.next());
            if (n == null) {
                return null;
            }
            next = n;
        }
        return next;
    }

    default public Stream<SpriteItem> getIngredientsInInventory() {
        return itemStackToTarget(this.getIngredients(), Caches.forInventory());
    }

    default public Stream<SpriteItem> getToolsInInventory() {
        return itemStackToTarget(this.getTools(), Caches.forInventory());
    }

    public static Stream<SpriteItem> itemStackToTarget(Stream<ItemStack> items, ItemCache cache) {
        return items.map(i -> cache.getCurrent(i.getMaterial().toInquiry())).flatMap(Function.identity());
    }

    /**
     *
     *
     * @return
     */
    default public int getToolSpace() {
        return this.getTools()
                .map(i -> i.getMaterial().isStackable() ? 1 : i.getQuantity())
                .reduce(0, Integer::sum);
    }

    /**
     * The number of recipes gained out of a tool's lifetime
     *
     * @return The number of uses before a tool expires. Values <=0 are treated as
     *         indefinite
     */
    default public int recipesPerTool() {
        return Integer.MAX_VALUE;
    }

    /**
     * Returns whether, upon a tool's expiry, the tool is removed from the inventory
     * or not (e.g. converted into an inert/broken form of the tool)
     *
     * @return {@code true} for expiration on use, IF the tool expires
     */
    default public boolean toolExpiresIntoNothing() {
        return true;
    }

    /**
     * Space required for a single iteration of this recipe
     *
     * @return The total space required, in slots
     */
    default public int getRecipeSpace() {
        return this.getIngredients()
                .map(i -> i.getMaterial().isStackable() ? 1 : i.getQuantity())
                .reduce(0, Integer::sum);
    }

    default public boolean usesTools() {
        return this.getToolSpace() > 0;
    }

    //represents chat cache ids for cooking/smelting/etc
    public int getContainerId();

    /**
     * Remaining recipe completions in inventory
     *
     * @return
     */
    default public int getRemainder() {
        return this.getFullUnit().map(i -> {
            int amt;
            if (i.getMaterial().isStackable()) {
                amt = Caches.forInventory().getCurrent(i.getMaterial().toInquiry())
                        .map(SpriteItem::getQuantity)
                        .reduce(0, Integer::sum);
            } else {
                Logging.info("Checking number of sprite items for: " + i.getMaterial());
                amt = Caches.forInventory().size(i.getMaterial().toInquiry());
            }
            return amt / i.getQuantity();
        }).min(Integer::compare).orElse(0);
    }

    default public Stream<ItemStack> getFullUnit() {
        int mult = this.recipesPerTool();
        if (mult > 28) {
            mult = 1;
        }
        int fmult = mult;
        return Stream.concat(this.getIngredients().map(i -> i.setQuantity(i.getQuantity() * fmult)), this.getTools());
    }

    //modifier will modify tools AND ingredients
    public Recipe modify(UnaryOperator<ItemStack> rawInputModifier);

    //return
    default public int getFullUnitSize() {
        return (this.getRecipeSpace() * this.recipesPerTool()) + this.getToolSpace();
    }

    public Recipe setTools(List<ItemStack> tools);
    public Recipe setIngredients(List<ItemStack> tools);
    public Recipe setOutput(List<ItemStack> tools);

    @Override
    default public Stream<ItemStack> fullInventoryWithdrawl(int space) {
        int count = space - this.getToolSpace() - (int) this.getIngredients().filter(ItemStack::isStackable).count();
        count /= this.getIngredientCount();
        int fcount = count;
        //return this.getFullUnit().map(i -> i.setQuantity(i.getQuantity() * count));
        return Stream.concat(Stream.concat(
                this.getIngredients().filter(ItemStack::isStackable).map(i -> i.setQuantity(Integer.MAX_VALUE)),
                this.getTools()),
                    this.getIngredients().filter(i -> !i.isStackable()).map(i -> i.setQuantity(i.getQuantity() * fcount)));
    }

    /**
     * Whether the inventory will be consumed automatically (e.g. cooking)
     *
     * @return {@code true} for an automatically consumed inventory with
     *         zero need for click intervention, false otherwise
     */
    public boolean isAutomatic();

    default public boolean hasContainer() {
        return this.getContainerId() != -1;
    }

    public RecipeType getRecipeType();
}
