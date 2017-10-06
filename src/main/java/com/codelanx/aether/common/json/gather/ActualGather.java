package com.codelanx.aether.common.json.gather;

import com.codelanx.aether.common.json.entity.Entity;
import com.codelanx.aether.common.json.gather.meta.GatherMeta;
import com.codelanx.aether.common.json.item.ItemStack;
import com.codelanx.aether.common.json.item.Material;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.codelanx.aether.common.json.region.Region;

import java.util.List;
import java.util.stream.Stream;

public class ActualGather implements Gather {

    //private final String name;
    //private final List<ItemStack> tools;



    /*

    {
      "name": "Barbarian fishing",
      "tools": [11323, 314],
      "drop": [11324, 11326, 11328, 11330, 11332],
      "bank": [],
      "recipes": ["Gut Leaping Trout", "Gut Leaping Salmon", "Gut Leaping Sturgeon"],
      "produced": [11328, 11330, 11332],
      "areas": [
        {
          "name": "Barbarian Outpost",
          "min": {
            "x": 2499,
            "y": 3490,
            "z": 0
          },
          "max": {
            "x": 2507,
            "y": 3513,
            "z": 0
          }
        }
      ],
      "target": [20926],
      "meta": {
        "uses-recipes": {
          "default": true,
          "label": "Gut fish",
          "//TODO": "type information"
        }
      }
    }
     */

    @Override
    public Stream<Recipe> getRecipes() {
        return null;
    }

    @Override
    public Stream<ItemStack> getTools() {
        return null;
    }

    @Override
    public Stream<Material> getBankedItems() {
        return null;
    }

    @Override
    public Stream<Material> getDroppedItems() {
        return null;
    }

    @Override
    public Stream<GatherMeta> getAllMeta() {
        return null;
    }

    @Override
    public GatherMeta getMeta(String key) {
        return null;
    }

    @Override
    public Stream<Entity<?, ?>> getTargets() {
        return null;
    }

    @Override
    public Stream<ItemStack> getProducedItems() {
        return null;
    }

    @Override
    public Stream<GatherMeta> getMetadata() {
        return null;
    }

    @Override
    public Stream<Region> getRegions() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }
}
