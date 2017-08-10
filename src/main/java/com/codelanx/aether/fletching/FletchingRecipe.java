package com.codelanx.aether.fletching;

import com.codelanx.aether.common.item.ItemStack;
import com.codelanx.aether.common.item.Material;
import com.codelanx.aether.common.recipe.Recipe;
import com.codelanx.aether.common.recipe.RecipeType;
import com.codelanx.aether.crafting.CraftingMaterial;

import java.util.Arrays;
import java.util.stream.Stream;

public enum FletchingRecipe implements Recipe {

    STEEL_ARROWS("Steel Arrows", -1, null, FletchingMaterial.STEEL_ARROWTIPS, FletchingMaterial.UNFINISHED_ARROW),
    ;

    private final String name;
    private final int parent;
    private final Material produces;
    //all items are used on first item
    private final ItemStack[] items;

    private FletchingRecipe(String name, int parentId, Material produces, Material... items) {
        this(name, parentId, produces, Arrays.stream(items).map(i -> new ItemStack(i, 1)).toArray(ItemStack[]::new));
    }

    private FletchingRecipe(String name, int parentId, Material produces, ItemStack... items) {
        this.name = name;
        this.parent = parentId;
        this.produces = produces;
        this.items = items;
    }

    public Stream<ItemStack> getRequiredItems() {
        return Arrays.stream(this.items);
    }

    @Override
    public int getContainerId() {
        return this.parent;
    }

    //null if not a recipe item
    public Material getProduct() {
        return this.produces;
    }

    public int getParentId() {
        return this.parent;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public RecipeType getRecipeType() {
        if (this.items.length > 1) {
            return RecipeType.COMBINE;
        }
        return RecipeType.COOK;
    }

    @Override
    public boolean isAutomatic() {
        return false;
    }

    @Override
    public int getIngrediateCount() {
        return this.items.length;
    }

    @Override
    public Stream<ItemStack> getIngredients() {
        return Arrays.stream(this.items);
    }

    @Override
    public Stream<ItemStack> getTools() {
        return Stream.empty();
    }

    @Override
    public int getToolSpace() {
        return 0;
    }

    public boolean isEnabled() {
        return this.parent > 0;
    }

}
