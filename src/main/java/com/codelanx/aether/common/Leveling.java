package com.codelanx.aether.common;

import com.codelanx.aether.common.json.item.Material;
import com.codelanx.aether.common.json.item.SerializableMaterial;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.codelanx.aether.common.json.recipe.SerializableRecipe;
import com.codelanx.commons.data.types.Json;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Leveling {

    private static final int[] levels = new int[] {
            0, 83, 174, 276, 388, 512, 650, 801, 969, 1154, //lvl 10
            1358, 1584, 1833, 2107, 2411, 2746, 3115, 3523, 3973, 4470, //lvl 20
            5018, 5624, 6291, 7028, 7842, 8740, 9730, 10824, 12031, 13363, //lvl 30
            14833, 16456, 18247, 20224, 22406, 24815, 27473, 30408, 33648, 37224, //lvl 40
            41171, 45529, 50339, 55649, 61512, 67983, 75127, 83014, 91721, 101333, //lvl 50
            111945, 123660, 136594, 150872, 166636, 184040, 203254, 224466, 247886, 273742, //lvl 60
            302288, 333804, 368599, 407015, 449428, 496254, 547953, 605032, 668051, 737627, //lvl 70
            814445, 899257, 992895, 1096278, 1210421, 1336443, 1475581, 1629200, 1798808, 1986068, //lvl 80
            2192818, 2421087, 2673114, 2951373, 3258594, 3597792, 3972294, 4385776, 4842295, 5346332, //lvl 90
            5902831, 6517253, 7195629, 7944614, 8771558, 9684577, 10692629, 11805606, 13034431, //lvl 99
    };

    public static double getLevel(int xp) {
        double lXp;
        for (int level = 1; level <= 99 && level < levels.length; level++) {
            lXp = levels[level];
            if (xp < lXp) {
                return level + ((xp - levels[level - 1]) / lXp);
            }
        }
        return levels.length;
    }


    public static void main(String... args) throws IOException {
        //exportMaterials(CraftingMaterial.values(), new File("resources/crafting/items.json"));
        //exportRecipes(CraftingRecipe.values(), new File("resources/crafting/recipes.json"));
        Byte b = null;
        System.out.println(b == 1);
    }

    private static void exportMaterials(Material[] values, File target) throws IOException {
        Json j = new Json();
        new File(target.getParent()).mkdirs();
        List<Material> back = Arrays.stream(values).map(SerializableMaterial::new).collect(Collectors.toList());
        j.set("items", back);
        j.save(target);
    }

    private static void exportRecipes(Recipe[] values, File target) throws IOException {
        Json j = new Json();
        new File(target.getParent()).mkdirs();
        List<Recipe> back = Arrays.stream(values).map(SerializableRecipe::new).collect(Collectors.toList());
        j.set("recipes", back);
        j.save(target);
    }

}
