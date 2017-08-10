package com.codelanx.aether.cooking;

import com.codelanx.aether.common.item.ItemStack;
import com.codelanx.aether.common.item.Material;
import com.codelanx.aether.common.recipe.Recipe;
import com.codelanx.aether.common.recipe.RecipeType;

import static com.codelanx.aether.cooking.CookingMaterial.*;
import java.util.Arrays;
import java.util.stream.Stream;

public enum BasicRecipe implements Recipe {

    //r prefix is for the import's sake, because it actually saves room in end

    R_BREAD_DOUGH("Bread dough", -1, BREAD_DOUGH, FLOUR, WATER),
    R_PITTA_DOUGH("Pitta dough", -1, PITTA_DOUGH, FLOUR, WATER),
    R_PASTRY_DOUGH("Pastry dough", -1, PASTRY_DOUGH, FLOUR, WATER),
    R_CAKE("Cake", -1, CAKE, CAKE_TIN, EGG, MILK, FLOUR),
    R_CHOCOLATE_CAKE("Chocolate cake", -1, null, CAKE, CHOCOLATE_BAR),
    R_SWEETCORN("Sweetcorn", -1, null, SWEETCORN),



    //pizza, ordered by lvl

    R_PIZZA_BASE("Pizza base", -1, PIZZA_BASE, FLOUR, WATER),
    R_INCOMPLETE_PIZZA("Incomplete Pizza", -1, INCOMPLETE_PIZZA, PIZZA_BASE, TOMATO),
    R_UNCOOKED_PIZZA("Uncooked pizza", -1, UNCOOKED_PIZZA, INCOMPLETE_PIZZA, CHEESE),
    R_PLAIN_PIZZA("Plain pizza", -1, PLAIN_PIZZA, UNCOOKED_PIZZA),
    R_MEAT_PIZZA("Plain pizza", -1, null, PLAIN_PIZZA),
    R_ANCHOVY_PIZZA("Anchovy pizza", -1, null, PLAIN_PIZZA, ANCHOVIES),
    R_PINEAPPLE_PIZZA("Pineapple pizza", -1, null, PLAIN_PIZZA, PINEAPPLE_RING), //TODO: Or PINEAPPLE_CHUNKS

    //pies, ordered by lvl

    R_UNCOOKED_BERRY_PIE("Uncooked berry pie", -1, UNCOOKED_BERRY_PIE, REDBERRIES, PIE_SHELL),
    R_REDBERRY_PIE("Redberry pie", -1, null, UNCOOKED_BERRY_PIE),
    R_UNCOOKED_MEAT_PIE("Uncooked meat pie", -1, UNCOOKED_MEAT_PIE, PIE_SHELL),
    R_MEAT_PIE("Meat pie", -1, null, UNCOOKED_MEAT_PIE),
    R_PART_MUD_PIE_1("Part mud pie", -1, PART_MUD_PIE_1, PIE_SHELL, COMPOST),
    R_PART_MUD_PIE_2("Part mud pie", -1, PART_MUD_PIE_2, PART_MUD_PIE_1, WATER),
    R_RAW_MUD_PIE("Raw mud pie", -1, RAW_MUD_PIE, PART_MUD_PIE_2, CLAY),
    R_MUD_PIE("Mud pie", -1, null, RAW_MUD_PIE),
    R_UNCOOKED_APPLE_PIE("Uncooked apple pie", -1, UNCOOKED_APPLE_PIE, PIE_SHELL, COOKING_APPLE),
    R_APPLE_PIE("Apple pie", -1, null, UNCOOKED_APPLE_PIE),
    R_PART_GARDEN_PIE_1("Part garden pie", -1, PART_GARDEN_PIE_1, PIE_SHELL, TOMATO),
    R_PART_GARDEN_PIE_2("Part garden pie", -1, PART_GARDEN_PIE_2, PART_GARDEN_PIE_1, ONION),
    R_RAW_GARDEN_PIE("Raw garden pie", -1, RAW_GARDEN_PIE, PART_GARDEN_PIE_2, CABBAGE),
    R_GARDEN_PIE("Garden pie", -1, null, RAW_GARDEN_PIE),
    R_PART_FISH_PIE_1("Part fish pie", -1, PART_FISH_PIE_1, PIE_SHELL, TROUT),
    R_PART_FISH_PIE_2("Part fish pie", -1, PART_FISH_PIE_2, PART_FISH_PIE_1, COD),
    R_RAW_FISH_PIE("Raw fish pie", -1, RAW_FISH_PIE, PART_FISH_PIE_2, POTATO),
    R_FISH_PIE("Fish pie", -1, null, RAW_FISH_PIE),
    R_UNCOOKED_BOTANICAL_PIE("Uncooked botanical pie", -1, UNCOOKED_BOTANICAL_PIE, PIE_SHELL, GOLOVANOVA_FRUIT_TOP),
    R_BOTANICAL_PIE("Botanical pie", -1, null, UNCOOKED_BOTANICAL_PIE),
    R_PART_ADMIRAL_PIE_1("Part admiral pie", -1, PART_ADMIRAL_PIE_1, PIE_SHELL, SALMON),
    R_PART_ADMIRAL_PIE_2("Part admiral pie", -1, PART_ADMIRAL_PIE_2, PART_ADMIRAL_PIE_1, TUNA),
    R_RAW_ADMIRAL_PIE("Raw admiral pie", -1, RAW_ADMIRAL_PIE, PART_ADMIRAL_PIE_2, POTATO),
    R_ADMIRAL_PIE("Admiral pie", -1, null, RAW_ADMIRAL_PIE),
    R_PART_WILD_PIE_1("Part wild pie", -1, PART_WILD_PIE_1, PIE_SHELL, RAW_BEAR_MEAT),
    R_PART_WILD_PIE_2("Part wild pie", -1, PART_WILD_PIE_2, PART_WILD_PIE_1, RAW_CHOMPY),
    R_RAW_WILD_PIE("Raw wild pie", -1, RAW_WILD_PIE, PART_WILD_PIE_2, RAW_RABBIT),
    R_WILD_PIE("Wild pie", -1, null, RAW_WILD_PIE),
    R_PART_SUMMER_PIE_1("Part summer pie", -1, PART_SUMMER_PIE_1, PIE_SHELL, STRAWBERRY),
    R_PART_SUMMER_PIE_2("Part summer pie", -1, PART_SUMMER_PIE_2, PART_SUMMER_PIE_1, WATERMELON),
    R_RAW_SUMMER_PIE("Raw summer pie", -1, RAW_SUMMER_PIE, PART_SUMMER_PIE_2, COOKING_APPLE),
    R_SUMMER_PIE("Summer pie", -1, null, RAW_SUMMER_PIE),

    //stews

    R_INCOMPLETE_STEW("Incomplete Stew", -1, INCOMPLETE_STEW, BOWL_OF_WATER, POTATO),
    R_UNCOOKED_STEW("Uncooked stew", -1, UNCOOKED_STEW, INCOMPLETE_STEW),
    R_STEW("Stew", -1, null, UNCOOKED_STEW),
    R_UNCOOKED_CURRY("Uncooked curry", -1, UNCOOKED_CURRY, UNCOOKED_STEW, SPICE), //TODO or 3 CURRY_LEAF
    R_CURRY_("Curry", -1, null, UNCOOKED_CURRY),

    //drinks

    R_JUG_OF_WINE("Jug of wine", -1, null, JUG_OF_WATER, GRAPES),
    R_WINE_OF_ZAMORAK("Wine of zamorak", -1, null, JUG_OF_WATER, ZAMORAKS_GRAPES),

    //fish, ordered by lvl

    R_SHRIMPS("Shrimp", -1, null, RAW_SHRIMPS),
    R_ANCHOVIES("Anchovies", -1, ANCHOVIES, RAW_ANCHOVIES),
    R_KARAMBWAN("Karambwan", -1, null, RAW_KARAMBWAN),
    R_HERRING("Herring", -1, null, RAW_HERRING),
    R_MACKEREL("Mackerel", -1, null, RAW_MACKEREL),
    R_TROUT("Trout", -1, TROUT, RAW_TROUT),
    R_COD("Cod", -1, COD, RAW_COD),
    R_PIKE("Pike", -1, null, RAW_PIKE),
    R_SALMON("Salmon", -1, SALMON, RAW_SALMON),
    R_TUNA("Tuna", -1, TUNA, RAW_TUNA),
    R_RAINBOW_FISH("Rainbow fish", -1, null, RAW_RAINBOW_FISH),
    R_LOBSTER("Lobster", 307, null, RAW_LOBSTER),
    R_BASS("Bass", -1, null, RAW_BASS),
    R_SWORDFISH("Swordfish", -1, null, RAW_SWORDFISH),
    R_MONKFISH("Monkfish", -1, null, RAW_MONKFISH),
    R_SHARK("Shark", -1, null, RAW_SHARK),
    R_SEA_TURTLE("Sea turtle", -1, null, RAW_SEA_TURTLE),
    R_ANGLERFISH("Anglerfish", -1, null, RAW_ANGLERFISH),
    R_DARK_CRAB("Dark Crab", -1, null, RAW_DARK_CRAB),
    R_MANTA_RAY("Manta Ray", -1, null, RAW_MANTA_RAY),

    //po-tay-toes
    R_BAKED_POTATO("Baked potato", -1, null, POTATO),
    R_POTATO_WITH_BUTTER("Potato with butter", -1, POTATO_WITH_BUTTER, BAKED_POTATO, PAT_OF_BUTTER),
    R_POTATO_WITH_CHEESE("Potato with cheese", -1, null, POTATO_WITH_BUTTER, CHEESE),

    //TODO: Toppings etc




    ;

    private final String name;
    private final int parent;
    private final Material produces;
    //all items are used on first item
    private final ItemStack[] items;

    private BasicRecipe(String name, int parentId, Material produces, Material... items) {
        this(name, parentId, produces, Arrays.stream(items).map(i -> new ItemStack(i, 1)).toArray(ItemStack[]::new));
    }

    private BasicRecipe(String name, int parentId, Material produces, ItemStack... items) {
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
        return true;
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

    public FoodType getResultType() {
        switch (this) {
            case R_LOBSTER:
                return FoodType.FISH;
        }
        return FoodType.OTHER;
    }

    public boolean isFinishedResult() {
        return this.produces == null;
    }

    //-1/0 for false, >0 for amount
    public int acceptsVariableFoodType(FoodType type) {
        switch (this) {
            case R_UNCOOKED_MEAT_PIE:
            case R_MEAT_PIZZA:
            case R_UNCOOKED_STEW:
                return type == FoodType.MEAT ? 1 : 0;
            default:
                return 0;
        }
    }

    //while nice, also super unnecessary and a waste of time
    /*public double getExperience() {
        switch (this) {
            case R_MEAT_PIZZA:
                return 26;
            case R_CHOCOLATE_CAKE:
                return 30;
            case R_ANCHOVY_PIZZA:
                return 39;
            case R_PINEAPPLE_PIZZA:
                return 52;
            case R_REDBERRY_PIE:
                return 78;
            case R_MEAT_PIE:
            case R_SWEETCORN:
                return 104;
            case R_RAINBOW_FISH:
                return 110;
            case R_LOBSTER:
                return 120;
            case R_MUD_PIE:
            case R_GARDEN_PIE:
                return 128;
            case R_APPLE_PIE:
            case R_BASS:
                return 130;
            case R_SWORDFISH:
                return 140;
            case R_PLAIN_PIZZA:
                return 143;
            case R_MONKFISH:
                return 150;
            case R_FISH_PIE:
                return 164;
            case R_CAKE:
            case R_BOTANICAL_PIE:
                return 180;
            case R_JUG_OF_WINE:
            case R_WINE_OF_ZAMORAK:
                return 200;
            case R_ADMIRAL_PIE:
            case R_SHARK:
                return 210;
            case R_SEA_TURTLE:
                return 211.3;
            case R_DARK_CRAB:
                return 215;
            case R_MANTA_RAY:
                return 216.2;
            case R_ANGLERFISH:
                return 230;
            case R_WILD_PIE:
                return 240;
            case R_SUMMER_PIE:
                return 260;
        }
        return 0;
    }*/

}
