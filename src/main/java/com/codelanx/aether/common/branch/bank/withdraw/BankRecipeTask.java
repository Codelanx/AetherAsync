package com.codelanx.aether.common.branch.bank.withdraw;

import com.codelanx.aether.common.json.item.ItemStack;
import com.codelanx.aether.common.json.recipe.Recipe;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BankRecipeTask extends BankItemsTask {

    public BankRecipeTask(Recipe recipe) {
        super(recipeToStacks(recipe));
    }

    private static List<ItemStack> recipeToStacks(Recipe recipe) {
        return Stream.concat(recipe.getTools(), recipe.getIngredients()).collect(Collectors.toList());
    }
}
