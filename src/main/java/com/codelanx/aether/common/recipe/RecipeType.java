package com.codelanx.aether.common.recipe;

public enum RecipeType {
    SMELT,
    CLICK,
    COOK,
    COMBINE,
    ;

    public static RecipeType infer(Recipe recipe) {
        if (recipe.getIngredientCount() > 1 || recipe.getToolSpace() > 0) {
            return RecipeType.COMBINE;
        }
        return RecipeType.CLICK;
    }
}
