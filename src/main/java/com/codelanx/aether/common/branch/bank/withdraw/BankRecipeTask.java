package com.codelanx.aether.common.branch.bank.withdraw;

import com.codelanx.aether.common.json.item.ItemStack;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.codelanx.commons.logging.Logging;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BankRecipeTask extends BankItemsTask {

    public BankRecipeTask(Recipe recipe) {
        super(recipeToStacks(recipe));
        Logging.info("\titems: [" + this.items.stream().map(ItemStack::toString).collect(Collectors.joining(",")) + "]");
    }

    private static List<ItemStack> recipeToStacks(Recipe recipe) {
        Logging.info("New BankRecipeTask:");
        int tC = recipe.getToolSpace();
        int nonStackableCount = recipe.getIngredients().filter(i -> !i.isStackable()).map(ItemStack::getQuantity).reduce(0, Integer::sum);
        int stackable = (int) recipe.getIngredients().filter(ItemStack::isStackable).count();
        int tot = (28 - (tC + stackable)) / (nonStackableCount <= 0 ? 1 : nonStackableCount);
        List<ItemStack> back = Stream.concat(recipe.getTools(), recipe.getIngredients().map(i -> i.setQuantity(i.isStackable() ? Integer.MAX_VALUE : i.getQuantity() * tot))).collect(Collectors.toList());
        Logging.info("\tReturning: " + back);
        return back;
    }
}
