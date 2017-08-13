package com.codelanx.aether.common.item;

import com.codelanx.aether.common.bot.async.AetherAsyncBot;
import com.codelanx.commons.data.FileDataType;
import com.codelanx.commons.data.types.Json;
import com.runemate.game.api.hybrid.Environment;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemLoader {

    private final Map<Integer, Material> materials = new HashMap<>();

    public ItemLoader(AetherAsyncBot bot) {
        File f = new File(bot.getResourcePath(), "items.json");
        if (!f.exists()) {
            Environment.getLogger().info("No resource items found, shutting down...");
            bot.stop();
            return;
        }
        Json json = FileDataType.newInstance(Json.class, f);
        if (json.getMutable("items").get() == null) {
            Environment.getLogger().info("No resource items found, shutting down...");
            bot.stop();
            return;
        }
        List<SerializableMaterial> items = json.getMutable("items").as(List.class, SerializableMaterial.class);
        items.forEach(i -> this.materials.put(i.getId(), i));
        if (this.materials.isEmpty()) {
            Environment.getLogger().info("No items found, continuing anyway...");
        } else {
            Environment.getLogger().info("Loaded " + this.materials.size() + " items");
        }
    }

    public Material getItem(int id) {
        return this.materials.get(id);
    }
}
