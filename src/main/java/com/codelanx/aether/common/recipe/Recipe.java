package com.codelanx.aether.common.recipe;

import com.codelanx.aether.common.bot.async.Aether;
import com.codelanx.aether.common.bot.async.AetherAsyncBot;
import com.codelanx.aether.common.item.ItemStack;
import com.codelanx.aether.bots.cooking.CookingBot;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Recipe {

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

    default public SpriteItemQueryResults getIngredientsInInventory() {
        return itemStackToTarget(this.getIngredients(), Inventory.newQuery());
    }

    default public SpriteItemQueryResults getToolsInInventory() {
        return itemStackToTarget(this.getTools(), Inventory.newQuery());
    }

    public static SpriteItemQueryResults itemStackToTarget(Stream<ItemStack> items, SpriteItemQueryBuilder builder) {
        Map<Integer, String> ids = items.collect(Collectors.toMap(ItemStack::getId, i -> i.getMaterial().getName()));
        return builder
                .ids(ids.keySet().stream().mapToInt(Integer::intValue).toArray())
                .names(ids.values().toArray(new String[ids.values().size()]))
                .results();
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

    //represents chat container ids for cooking/smelting/etc
    public int getContainerId();

    /**
     * Remaining recipe completions in inventory
     *
     * @return
     */
    default public int getRemainder() {
        return this.getFullUnit().map(i -> {
            return Aether.getBot().getInventory().get(i.getMaterial()) / i.getQuantity();
        }).min(Integer::compare).orElse(0);
    }

    default public Stream<ItemStack> getFullUnit() {
        int size = this.getFullUnitSize();
        int count = 28 / size;
        int mult = this.recipesPerTool();
        return Stream.concat(this.getIngredients().map(i -> i.setQuantity(i.getQuantity() * mult)), this.getTools());
    }

    //return
    default public int getFullUnitSize() {
        return (this.getRecipeSpace() * this.recipesPerTool()) + this.getToolSpace();
    }

    default public Stream<ItemStack> fullInventoryWithdrawl() {
        int count = 28 / this.getFullUnitSize();
        return this.getFullUnit().map(i -> i.setQuantity(i.getQuantity() * count));
    }

    /**
     * Whether the inventory will be consumed automatically (e.g. cooking)
     *
     * @return {@code true} for an automatically consumed inventory with
     *         zero need for click intervention, false otherwise
     */
    public boolean isAutomatic();

    public RecipeType getRecipeType();
}
