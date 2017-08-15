package com.codelanx.aether.common.json.recipe;

import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.json.item.ItemLoader;
import com.codelanx.aether.common.json.item.ItemStack;
import com.codelanx.commons.data.FileSerializable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SerializableRecipe implements FileSerializable, Recipe {

    private final String name;
    private final int containerId;
    private final boolean automatic;
    private final RecipeType type;
    private final List<ItemStack> ingredients;
    private final List<ItemStack> tools;

    public SerializableRecipe(String name, int containerId, boolean automatic, RecipeType type, Map<Integer, Integer> ingredients, Map<Integer, Integer> tools) {
        this.name = name;
        this.containerId = containerId;
        this.automatic = automatic;
        this.type = type;
        ItemLoader items = Aether.getBot().getKnownItems();
        this.ingredients = ingredients.entrySet().stream().collect(
                Collectors.toMap(
                        e -> items.getItem(e.getKey()),
                        Entry::getValue
                )).entrySet().stream().map(ent -> new ItemStack(ent.getKey(), ent.getValue())).collect(Collectors.toList());
        this.tools = tools.entrySet().stream().collect(
                Collectors.toMap(
                        e -> items.getItem(e.getKey()),
                        Entry::getValue
                )).entrySet().stream().map(ent -> new ItemStack(ent.getKey(), ent.getValue())).collect(Collectors.toList());
    }

    public SerializableRecipe(Map<String, Object> data) {
        this.name = (String) data.get("name");
        this.containerId = ((Long) data.get("container")).intValue();
        this.automatic = (Boolean) data.get("automatic");
        ItemLoader items = Aether.getBot().getKnownItems();
        this.ingredients = ((Map<String, Object>) data.get("ingredients")).entrySet().stream().collect(
                Collectors.toMap(
                        e -> items.getItem(Integer.parseInt(e.getKey())),
                        Entry::getValue
                )).entrySet().stream().map(ent -> new ItemStack(ent.getKey(), Integer.parseInt(String.valueOf(ent.getValue())))).collect(Collectors.toList());
        this.tools = ((Map<String, Object>) data.get("tools")).entrySet().stream().collect(
                Collectors.toMap(
                        e -> items.getItem(Integer.parseInt(e.getKey())),
                        Entry::getValue
                )).entrySet().stream().map(ent -> new ItemStack(ent.getKey(), Integer.parseInt(String.valueOf(ent.getValue())))).collect(Collectors.toList());
        this.type = RecipeType.valueOf(Optional.ofNullable((String) data.get("type")).map(String::toUpperCase).orElse(RecipeType.infer(this).name()));
    }

    public SerializableRecipe(Recipe other) {
        this.name = other.getName();
        this.automatic = other.isAutomatic();
        this.containerId = other.getContainerId();
        this.ingredients = other.getIngredients().collect(Collectors.toList());
        this.tools = other.getTools().collect(Collectors.toList());
        this.type = other.getRecipeType();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Stream<ItemStack> getIngredients() {
        return this.ingredients.stream();
    }

    @Override
    public int getIngredientCount() {
        return this.tools.size();
    }

    @Override
    public Stream<ItemStack> getTools() {
        return this.tools.stream();
    }

    @Override
    public int getContainerId() {
        return this.containerId;
    }

    @Override
    public boolean isAutomatic() {
        return this.automatic;
    }

    @Override
    public RecipeType getRecipeType() {
        return this.type;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> back = new LinkedHashMap<>();
        back.put("name", this.name);
        back.put("type", this.type);
        back.put("automatic", this.automatic);
        back.put("container", this.containerId);
        back.put("ingredients", this.ingredients.stream().collect(Collectors.toMap(i -> String.valueOf(i.getMaterial().getId()), ItemStack::getQuantity)));
        back.put("tools", this.tools.stream().collect(Collectors.toMap(i -> String.valueOf(i.getMaterial().getId()), ItemStack::getQuantity)));
        return back;
    }
}
