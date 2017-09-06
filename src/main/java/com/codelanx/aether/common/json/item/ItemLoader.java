package com.codelanx.aether.common.json.item;

import com.codelanx.aether.common.bot.AsyncBot;
import com.codelanx.commons.data.FileDataType;
import com.codelanx.commons.data.types.Json;
import com.codelanx.commons.logging.Logging;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemLoader {

    private final Map<Integer, Material> materials = new HashMap<>();

    public ItemLoader(AsyncBot bot) {
        File f = new File(bot.getResourcePath(), "items.json");
        if (!f.exists()) {
            Logging.info("No resource items found, shutting down...");
            bot.stop();
            return;
        }
        Json json = FileDataType.newInstance(Json.class, f);
        if (json.getMutable("items").get() == null) {
            Logging.info("No resource items found, shutting down...");
            bot.stop();
            return;
        }
        List<SerializableMaterial> items = json.getMutable("items").as(List.class, SerializableMaterial.class);
        items.forEach(i -> this.materials.put(i.getId(), i));
        if (this.materials.isEmpty()) {
            Logging.info("No items found, continuing anyway...");
        } else {
            Logging.info("Loaded " + this.materials.size() + " items");
        }
    }

    public ItemLoader(File f) {
        Json json = FileDataType.newInstance(Json.class, f);
        if (json.getMutable("items").get() == null) {
            Logging.info("No resource items found, shutting down...");
            return;
        }
        List<SerializableMaterial> items = json.getMutable("items").as(List.class, SerializableMaterial.class);
        items.forEach(i -> this.materials.put(i.getId(), i));
        if (this.materials.isEmpty()) {
            Logging.info("No items found, continuing anyway...");
        } else {
            Logging.info("Loaded " + this.materials.size() + " items");
        }
    }

    public Material getItem(int id) {
        return this.materials.get(id);
    }
}
