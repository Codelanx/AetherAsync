package com.codelanx.aether.common.json.item;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.Item;
import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ItemStack implements Item, Cloneable {

    private Material material;
    private final int amount;

    public ItemStack(Material material) {
        this(material, 1);
    }

    public ItemStack(Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }

    @Override
    public int getId() {
        return this.material.getId();
    }

    public Material getMaterial() {
        return this.material;
    }
    
    public boolean isStackable() {
        return this.material.isStackable();
    }
    
    public boolean isEquippable() {
        return this.material.isEquippable();
    }

    @Override
    public ItemDefinition getDefinition() {
        return ItemDefinition.get(this.getId());
    }

    @Override
    public int getQuantity() {
        return this.amount;
    }

    @Override
    public String toString() {
        return "ItemStack{" + material.getName() + "x" + this.amount + "}";
    }

    public ItemStack setQuantity(int amount) {
        return new ItemStack(this.material, amount);
    }

    @Override
    protected ItemStack clone() {
        try {
            return (ItemStack) super.clone();
        } catch (CloneNotSupportedException e) {
            Environment.getLogger().info(this.getClass().getName() + " does not support cloneable, but should");
            e.printStackTrace();
        }
        return new ItemStack(this.material, this.amount);
    }

    public static Collector<ItemStack, ?, Map<Material, Integer>> toSummingMap() {
        return Collectors.toMap(ItemStack::getMaterial, ItemStack::getQuantity, Integer::sum, HashMap::new);
    }
}
