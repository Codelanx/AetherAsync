package com.codelanx.aether.common.recipe;

import com.codelanx.aether.common.item.ItemStack;
import com.codelanx.aether.common.item.Material;

public class RecipeComponent extends ItemStack {

    public RecipeComponent(Material material) {
        super(material);
    }

    public RecipeComponent(Material material, int amount) {
        super(material, amount);
    }



}
