package com.codelanx.aether.common.json.recipe;

import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.json.item.ItemLoader;
import com.codelanx.aether.common.json.item.ItemStack;
import com.codelanx.aether.common.rest.RestLoader;
import com.codelanx.commons.data.FileSerializable;

import java.util.Collections;
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
    private final List<ItemStack> output;
    private final List<ItemStack> ingredients;
    private final List<ItemStack> tools;

    public SerializableRecipe(String name, int containerId, boolean automatic, RecipeType type, Map<Integer, Integer> ingredients, Map<Integer, Integer> tools, Map<Integer, Integer> output) {
        this.output = this.fromIntMap(output);
        this.name = Optional.ofNullable(name).orElseGet(() -> this.output.isEmpty() ? null : this.output.get(0).getMaterial().getName());
        this.containerId = containerId;
        this.automatic = automatic;
        this.type = type;
        this.ingredients = this.fromIntMap(ingredients);
        this.tools = this.fromIntMap(tools);
    }

    public SerializableRecipe(Map<String, Object> data) {
        this.output = this.fromMap((Map<String, Object>) data.get("output"));
        this.name = Optional.ofNullable((String) data.get("name")).orElseGet(() -> this.output.isEmpty() ? null : this.output.get(0).getMaterial().getName());
        this.containerId = Optional.ofNullable((Long) data.get("container")).map(Long::intValue).orElse(-1);
        this.automatic = Optional.ofNullable((Boolean) data.get("automatic")).orElse(false);
        this.ingredients = this.fromMap((Map<String, Object>) data.get("ingredients"));
        this.tools = this.fromMap((Map<String, Object>) data.get("tools"));
        this.type = Optional.ofNullable((String) data.get("type")).map(String::toUpperCase).map(RecipeType::valueOf).orElseGet(() -> RecipeType.infer(this));
    }

    public SerializableRecipe(Recipe other) {
        this.name = other.getName();
        this.automatic = other.isAutomatic();
        this.containerId = other.getContainerId();
        this.ingredients = other.getIngredients().collect(Collectors.toList());
        this.tools = other.getTools().collect(Collectors.toList());
        this.type = other.getRecipeType();
        this.output = other.getOutput().collect(Collectors.toList());
    }

    private List<ItemStack> fromMap(Map<String, Object> map) {
        return map == null ? Collections.emptyList() : map.entrySet().stream().collect(
                Collectors.toMap(
                        e -> Aether.getBot().getData().getItem(Integer.parseInt(e.getKey())),
                        e -> Integer.parseInt(String.valueOf(e.getValue()))
                )).entrySet().stream().map(ent -> new ItemStack(ent.getKey(), ent.getValue())).collect(Collectors.toList());
    }

    private List<ItemStack> fromIntMap(Map<Integer, Integer> ids) {
        RestLoader loader = Aether.getBot().getData();
        return ids.entrySet().stream().collect(
                Collectors.toMap(
                        e -> loader.getItem(e.getKey()),
                        Entry::getValue
                )).entrySet().stream().map(ent -> new ItemStack(ent.getKey(), ent.getValue())).collect(Collectors.toList());
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
        return this.ingredients.size();
    }

    @Override
    public Stream<ItemStack> getTools() {
        return this.tools.stream();
    }

    @Override
    public Stream<ItemStack> getOutput() {
        return this.output.stream();
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
        back.put("output", this.output);
        back.put("ingredients", this.ingredients.stream().collect(Collectors.toMap(i -> String.valueOf(i.getMaterial().getId()), ItemStack::getQuantity)));
        back.put("tools", this.tools.stream().collect(Collectors.toMap(i -> String.valueOf(i.getMaterial().getId()), ItemStack::getQuantity)));
        return back;
    }

    @Override
    public String toString() {
        return "SerializableRecipe{" +
                "output=" + output +
                ", name='" + name + '\'' +
                ", containerId=" + containerId +
                ", automatic=" + automatic +
                ", type=" + type +
                ", ingredients=" + ingredients +
                ", tools=" + tools +
                '}';
    }
}
