package com.codelanx.aether.common.json.gather;

import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.json.entity.Entity;
import com.codelanx.aether.common.json.gather.meta.GatherMeta;
import com.codelanx.aether.common.json.item.ItemLoader;
import com.codelanx.aether.common.json.item.ItemStack;
import com.codelanx.aether.common.json.item.Material;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.codelanx.aether.common.json.region.Region;
import com.codelanx.commons.data.FileSerializable;
import com.runemate.game.api.hybrid.location.Area;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SerializableGather implements FileSerializable, Gather {

    private final String name;
    private final List<ItemStack> tools;

    public SerializableGather(String name, Map<Integer, Integer> tools) {
        this.name = name;
        ItemLoader items = Aether.getBot().getData().getKnownItems();
        this.tools = tools.entrySet().stream().collect(
                Collectors.toMap(
                        e -> items.getItem(e.getKey()),
                        Entry::getValue
                )).entrySet().stream().map(ent -> new ItemStack(ent.getKey(), ent.getValue())).collect(Collectors.toList());
    }

    public SerializableGather(Map<String, Object> data) {
        this.name = (String) data.get("name");
        ItemLoader items = Aether.getBot().getData().getKnownItems();
        this.tools = ((Map<String, Object>) data.get("tools")).entrySet().stream().collect(
                Collectors.toMap(
                        e -> items.getItem(Integer.parseInt(e.getKey())),
                        Entry::getValue
                )).entrySet().stream().map(ent -> new ItemStack(ent.getKey(), Integer.parseInt(String.valueOf(ent.getValue())))).collect(Collectors.toList());
    }

    public SerializableGather(Gather other) {
        this.name = other.getDisplayName();
        this.tools = other.getTools().collect(Collectors.toList());
    }

    @Override
    public String getDisplayName() {
        return this.name;
    }

    @Override
    public Stream<Recipe> getRecipes() {
        return null;
    }

    @Override
    public Stream<ItemStack> getTools() {
        return this.tools.stream();
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
    public Map<String, Object> serialize() {
        Map<String, Object> back = new LinkedHashMap<>();
        back.put("name", this.name);
        back.put("tools", this.tools.stream().collect(Collectors.toMap(i -> String.valueOf(i.getMaterial().getId()), ItemStack::getQuantity)));
        return back;
    }
}
