package com.codelanx.aether.common.json.recipe;

import com.codelanx.aether.common.bot.AsyncBot;
import com.codelanx.aether.common.rest.Loader;
import com.codelanx.commons.data.FileDataType;
import com.codelanx.commons.data.types.Json;
import com.codelanx.commons.logging.Logging;
import com.codelanx.commons.util.Scheduler;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecipeLoader implements Loader {

    private final Map<String, Recipe> recipes = new HashMap<>();
    private final AsyncBot bot;
    private final File f;

    public RecipeLoader(AsyncBot bot) {
        this.f = new File(bot.getResourcePath(), "recipes.json");
        this.bot = bot;
        if (!f.exists()) {
            Logging.info("No resource for recipes found, shutting down...");
            bot.stop();
        }
    }

    public Recipe getRecipe(String name) {
        return this.recipes.get(name);
    }

    @Override
    public void loadLocal() {
        Json json = FileDataType.newInstance(Json.class, f);
        if (json.getMutable("recipes").get() == null) {
            Logging.info("No resource for recipes found, shutting down...");
            bot.stop();
            return;
        }
        List<SerializableRecipe> raw = json.getMutable("recipes").as(List.class, SerializableRecipe.class);
        raw.forEach(i -> this.recipes.put(i.getName(), i));
        if (this.recipes.isEmpty()) {
            Logging.info("No recipes found, continuing anyway...");
        } else {
            this.recipes.values().forEach(System.out::println);
            Logging.info("Loaded " + this.recipes.size() + " recipes");
        }
    }
}