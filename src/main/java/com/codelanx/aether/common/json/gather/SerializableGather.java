package com.codelanx.aether.common.json.gather;

import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.json.item.ItemLoader;
import com.codelanx.aether.common.json.item.ItemStack;
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
    private final boolean automatic;
    private final List<ItemStack> tools;

    public SerializableGather(String name, boolean automatic, Map<Integer, Integer> tools) {
        this.name = name;
        this.automatic = automatic;
        ItemLoader items = Aether.getBot().getKnownItems();
        this.tools = tools.entrySet().stream().collect(
                Collectors.toMap(
                        e -> items.getItem(e.getKey()),
                        Entry::getValue
                )).entrySet().stream().map(ent -> new ItemStack(ent.getKey(), ent.getValue())).collect(Collectors.toList());
    }

    public SerializableGather(Map<String, Object> data) {
        this.name = (String) data.get("name");
        this.automatic = (Boolean) data.get("automatic");
        ItemLoader items = Aether.getBot().getKnownItems();
        this.tools = ((Map<String, Object>) data.get("tools")).entrySet().stream().collect(
                Collectors.toMap(
                        e -> items.getItem(Integer.parseInt(e.getKey())),
                        Entry::getValue
                )).entrySet().stream().map(ent -> new ItemStack(ent.getKey(), Integer.parseInt(String.valueOf(ent.getValue())))).collect(Collectors.toList());
    }

    public SerializableGather(Gather other) {
        this.name = other.getName();
        this.automatic = other.isAutomatic();
        this.tools = other.getTools().collect(Collectors.toList());
    }

    @Override
    public Area getArea() {
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Stream<ItemStack> getTools() {
        return this.tools.stream();
    }

    @Override
    public boolean isAutomatic() {
        return this.automatic;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> back = new LinkedHashMap<>();
        back.put("name", this.name);
        back.put("automatic", this.automatic);
        back.put("tools", this.tools.stream().collect(Collectors.toMap(i -> String.valueOf(i.getMaterial().getId()), ItemStack::getQuantity)));
        return back;
    }
}
