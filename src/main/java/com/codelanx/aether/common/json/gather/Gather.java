package com.codelanx.aether.common.json.gather;

import com.codelanx.aether.common.json.UserNameable;
import com.codelanx.aether.common.json.entity.Entity;
import com.codelanx.aether.common.json.gather.meta.GatherMeta;
import com.codelanx.aether.common.json.item.ItemStack;
import com.codelanx.aether.common.json.item.Material;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.codelanx.aether.common.json.region.Region;
import com.runemate.game.api.hybrid.location.Area;

import java.util.stream.Stream;

public interface Gather extends UserNameable {

    //TODO: Required non-recipe items

    public Stream<Recipe> getRecipes();

    //negative amounts are special
    //-1 == as many as possible
    public Stream<ItemStack> getTools();

    public Stream<Material> getBankedItems();

    public Stream<Material> getDroppedItems();

    public Stream<GatherMeta> getAllMeta();

    public GatherMeta getMeta(String key);

    public Stream<Entity<?, ?>> getTargets();

    public Stream<ItemStack> getProducedItems();

    public Stream<GatherMeta> getMetadata();

    public Stream<Region> getRegions();

    default public boolean hasRecipes() {
        return this.getRecipes().count() <= 0;
    }

}
