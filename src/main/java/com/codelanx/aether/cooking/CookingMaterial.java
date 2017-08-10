package com.codelanx.aether.cooking;

import com.codelanx.aether.common.item.Material;

public enum CookingMaterial implements Material {

    //general/other

    JUG_OF_WATER(1937, "Jug of water"),
    BUCKET_OF_WATER(1929, "Bucket of water"),
    WATER(-1, null), //TODO: multi-item support
    MILK(1927, "Bucket of milk"),
    FLOUR(1933, "Pot of flour"),
    POT_OF_CREAM(2130, "Pot of cream"),
    PAT_OF_BUTTER(6697, "Pat of butter"),
    CHEESE(1985, "Cheese"),
    TOMATO(1982, "Tomato"),
    POTATO(1942, "Potato"),
    GRAPES(1987, "Grapes"),
    STRAWBERRY(5504, "Strawberry"),
    WATERMELON(5982, "Watermelon"),
    ONION(1957, "Onion"),
    CABBAGE(1965, "Cabbage"),
    COOKING_APPLE(1955, "Cooking apple"),
    COMPOST(6032, "Compost"), //mud pie
    CLAY(434, "Clay"), //mud pie
    ZAMORAKS_GRAPES(20749, "Zamorak's grapes"),
    REDBERRIES(1951, "Redberries"),
    CHOCOLATE_BAR(1973, "Chocolate bar"),
    PINEAPPLE(2114, "Pineapple"),
    PINEAPPLE_RING(2118, "Pineapple ring"),
    PINEAPPLE_CHUNKS(2116, "Pineapple chunks"),
    EGG(1944, "Egg"),
    SWEETCORN(5986, "Sweetcorn"),

    //dishes/tools

    PIE_DISH(2313, "Pie dish"),
    CAKE_TIN(1887, "Cake tin"),
    KNIFE(946, "Knife"),

    //meats

    RAW_BEEF(2132, "Raw beef"),
    RAW_YAK_MEAT(10816, "Raw yak meat"),
    RAW_BEAR_MEAT(2136, "Raw bear meat"),
    RAW_RAT_MEAT(2134, "Raw rat meat"),
    RAW_UGTHANKI_MEAT(-1, "Raw ugthanki meat"), //TODO
    RAW_CHICKEN(2138, "Raw chicken"),
    RAW_BIRD_MEAT(997, "Raw bird meat"),
    RAW_CHOMPY(2876, "Raw chompy"),
    RAW_OOMLIE(2337, "Raw oomlie"), //bird thing
    RAW_RABBIT(3226, "Raw rabbit"),
    RAW_CAVE_EEL(5001, "Raw cave eel"),
    RAW_SLIMY_EEL(3379, "Raw slimy eel"),


    //doughs/pastry bases

    BREAD_DOUGH(2307, "Bread dough"),
    PASTRY_DOUGH(1953, "Pastry dough"),
    PIZZA_BASE(2283, "Pizza base"),
    PITTA_DOUGH(1863, "Pitta dough"),
    UNCOOKED_CAKE(1889, "Uncooked cake"), //consumes egg, milk, and flour simultaneously
    CAKE(1891, "Cake"),

    //pizza

    INCOMPLETE_PIZZA(2285, "Incomplete pizza"),
    UNCOOKED_PIZZA(2287, "Uncooked pizza"),
    PLAIN_PIZZA(2289, "Plain pizza"),
    MEAT_PIZZA(2293, "Meat pizza"),
    ANCHOVY_PIZZA(2297, "Anchovy pizza"),
    PINEAPPLE_PIZZA(2301, "Pineapple pizza"),

    //potatoes, and their toppings

    BAKED_POTATO(6701, "Baked potato"),
    POTATO_WITH_BUTTER(6703, "Potato with butter"),
    /*SPICY_SAUCE(),
    CHILLI_CON_CARNE(),
    SCRAMBLED_EGG(),
    SCRAMBLED_EGG_AND_TOMATO(),
    FRIED_ONION(),
    FRIED_MUSHROOM(),
    FRIED_MUSHROOM_AND_ONION(),
    TUNA_AND_SWEETCORN(),*/

    //SWEETCORN(), the non-raw variety, do we need this?

    //drinks



    //pies

    PIE_SHELL(2315, "Pie shell"),
    UNCOOKED_BERRY_PIE(2321, "Uncooked berry pie"),
    UNCOOKED_MEAT_PIE(2319, "Uncooked meat pie"),
    PART_MUD_PIE_1(7164, "Part mud pie"),
    PART_MUD_PIE_2(7166, "Part mud pie"),
    RAW_MUD_PIE(7168, "Raw mud pie"),
    UNCOOKED_APPLE_PIE(2317, "Uncooked apple pie"),
    PART_GARDEN_PIE_1(7172, "Part garden pie"),
    PART_GARDEN_PIE_2(7174, "Part garden pie"),
    RAW_GARDEN_PIE(7176, "Raw garden pie"),
    PART_FISH_PIE_1(7182, "Part fish pie"),
    PART_FISH_PIE_2(7184, "Part fish pie"),
    RAW_FISH_PIE(7186, "Raw fish pie"),
    UNCOOKED_BOTANICAL_PIE(19656, "Uncooked botanical pie"),
    GOLOVANOVA_FRUIT_TOP(19653, "Golovanova fruit top"),
    PART_ADMIRAL_PIE_1(7192, "Part admiral pie"), //TODO: Verify
    PART_ADMIRAL_PIE_2(7194, "Part admiral pie"), //TODO: Verify
    RAW_ADMIRAL_PIE(7196, "Raw admiral pie"),
    PART_WILD_PIE_1(7202, "Part wild pie"), //TODO: Verify
    PART_WILD_PIE_2(7204, "Part wild pie"), //TODO: Verify
    RAW_WILD_PIE(7206, "Raw wild pie"),
    PART_SUMMER_PIE_1(7212, "Part summer pie"), //TODO: Verify
    PART_SUMMER_PIE_2(7214, "Part summer pie"), //TODO: Verify
    RAW_SUMMER_PIE(7216, "Raw summer pie"),

    //fish

    RAW_SHRIMPS(317, "Raw shrimps"),
    RAW_PIKE(349, "Raw pike"),
    RAW_BASS(363, "Raw bass"),
    RAW_ANCHOVIES(321, "Raw anchovies"),
    ANCHOVIES(319, "Anchovies"), //used in pizza
    RAW_KARAMBWAN(3142, "Raw karambwan"),
    RAW_MACKEREL(353, "Raw mackerel"),
    RAW_TROUT(335, "Raw trout"),
    TROUT(333, "Trout"), //fish pie
    RAW_HERRING(345, "Raw herring"),
    RAW_RAINBOW_FISH(10138, "Raw rainbow fish"),
    RAW_SALMON(331, "Raw salmon"),
    SALMON(329, "Salmon"), //admiral pie
    RAW_TUNA(359, "Raw tuna"),
    TUNA(361, "Tuna"), //admiral pie
    RAW_COD(341, "Raw cod"),
    COD(339, "Cod"), //fish pie
    RAW_LOBSTER(377, "Raw lobster", "Raw lobsters"),
    RAW_SWORDFISH(371, "Raw swordfish"),
    RAW_MONKFISH(7944, "Raw monkfish"),
    RAW_MANTA_RAY(389, "Raw manta ray"),
    RAW_SEA_TURTLE(395, "Raw sea turtle"),
    RAW_SHARK(383, "Raw shark"),
    RAW_DARK_CRAB(11934, "Raw dark crab"),
    RAW_ANGLERFISH(13439, "Raw anglerfish"),

    //non-wiki foods
    BOWL_OF_WATER(1921, "Bowl of water"),
    INCOMPLETE_STEW(1997, "Incomplete stew"),
    UNCOOKED_STEW(2001, "Uncooked stew"),
    CURRY_LEAF(5970, "Curry leaf"),
    SPICE(2007, "Spice"),
    UNCOOKED_CURRY(-1, "Uncooked curry"), //TODO (and stages between stew and curry)
    //STEW(),
    //CURRY(),

    //TODO: Spicy stew, gnome foods



    ;

    private final int id;
    private final String name;
    private final String plural;

    private CookingMaterial(int id, String name) {
        this(id, name, name);
    }

    private CookingMaterial(int id, String name, String plural) {
        this.id = id;
        this.name = name;
        this.plural = plural;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPlural() {
        return this.plural;
    }

    @Override
    public boolean isStackable() {
        return false;
    }

    public boolean lumbridgeCompatible() {
        switch (this) {
            case BREAD_DOUGH:
            case UNCOOKED_BERRY_PIE:
            case UNCOOKED_MEAT_PIE:
                return true;
        }
        return false;
    }

}
