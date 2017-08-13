package com.codelanx.aether.common.gather;

import com.codelanx.aether.common.bot.async.Aether;
import com.codelanx.aether.common.item.ItemStack;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Gather {

    //gets the name of the RECIPE, not relevant for in-game details
    public String getName();

    /**
     * Returns any required side-tools for the recipe (e.g. a knife or a bracelet mould)
     *
     * @return
     */
    public Stream<ItemStack> getTools();

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
    
    public Area getArea();

    /**
     * The number of recipes gained out of a tool's lifetime
     *
     * @return The number of uses before a tool expires. Values <=0 are treated as
     *         indefinite
     */
    default public int toolUses() {
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


    default public boolean usesTools() {
        return this.getToolSpace() > 0;
    }

    
    /**
     * Whether the inventory will be consumed automatically (e.g. cooking)
     *
     * @return {@code true} for an automatically consumed inventory with
     *         zero need for click intervention, false otherwise
     */
    public boolean isAutomatic();

}
