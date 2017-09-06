package com.codelanx.aether.common.json.gather;

import com.codelanx.aether.common.bot.AsyncBot;
import com.codelanx.commons.data.FileDataType;
import com.codelanx.commons.data.types.Json;
import com.codelanx.commons.logging.Logging;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GatherLoader {

    private final Map<String, Gather> recipes = new HashMap<>();

    public GatherLoader(AsyncBot bot) {
        File f = new File(bot.getResourcePath(), "recipes.json");
        if (!f.exists()) {
            Logging.info("No resource for recipes found, shutting down...");
            bot.stop();
            return;
        }
        Json json = FileDataType.newInstance(Json.class, f);
        if (json.getMutable("recipes").get() == null) {
            Logging.info("No resource for recipes found, shutting down...");
            bot.stop();
            return;
        }
        List<SerializableGather> items = json.getMutable("recipes").as(List.class, SerializableGather.class);
        items.forEach(i -> this.recipes.put(i.getName(), i));
        if (this.recipes.isEmpty()) {
            Logging.info("No recipes found, continuing anyway...");
        } else {
            Logging.info("Loaded " + this.recipes.size() + " recipes");
        }
    }

    public Gather getRecipe(String name) {
        return this.recipes.get(name);
    }
}