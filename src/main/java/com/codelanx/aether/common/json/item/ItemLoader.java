package com.codelanx.aether.common.json.item;

import com.codelanx.aether.common.bot.AsyncBot;
import com.codelanx.commons.data.FileDataType;
import com.codelanx.commons.data.types.Json;
import com.codelanx.commons.logging.Logging;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemLoader {

    private final Map<Integer, Material> materials = new HashMap<>();
    private final Map<String, Material> byName = new HashMap<>();

    public ItemLoader(AsyncBot bot) {
        this(bot, new File(bot.getResourcePath(), "items.json"));
    }

    public ItemLoader(AsyncBot bot, File f) {
        if (!f.exists()) {
            Logging.info("No resource items found, shutting down...");
            bot.stop();
            return;
        }
        Json json = FileDataType.newInstance(Json.class, f);
        if (json.getMutable("items").get() == null) {
            Logging.info("No resource items found, shutting down...");
            return;
        }
        List<SerializableMaterial> raw = json.getMutable("items").as(List.class, SerializableMaterial.class);
        raw.forEach(i -> this.materials.put(i.getId(), i));
        if (this.materials.isEmpty()) {
            Logging.info("No items found, continuing anyway...");
        } else {
            Logging.info("Loaded " + this.materials.size() + " items");
        }
    }

    public Material getItem(int id) {
        return this.materials.get(id);
    }

    public Material getItem(String name) {
        return this.byName.computeIfAbsent(name, k -> this.materials.values().stream().filter(m -> k.equals(m.getName())).findFirst().orElse(null));
    }
}
