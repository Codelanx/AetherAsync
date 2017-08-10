package com.codelanx.aether.construction.mission;

import com.codelanx.aether.construction.BasicBitchBot;
import com.codelanx.aether.construction.item.ConstructionMaterials;
import com.codelanx.aether.common.item.ItemStack;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceComponent.Type;

import java.util.Arrays;
import java.util.stream.Stream;

public enum CraftTarget {
    OAK_LARDER("Oak larder", Type.CONTAINER, FurnitureSpace.LARDER, 458, new ItemStack(ConstructionMaterials.OAK_PLANK, 8)),
    ;

    private final String name;
    private final Type type;
    private final Buildable furniture;
    private final int parent;
    private final ItemStack[] items;

    private CraftTarget(String name, Type type, Buildable furniture, int parentId, ItemStack... items) {
        this.name = name;
        this.type = type;
        this.furniture = furniture;
        this.parent = parentId;
        this.items = items;
    }

    public int getPossibleBuilds() {
        return this.getRequiredItems().map(i -> {
            return BasicBitchBot.get().getInventory().get(i.getMaterial()) / i.getQuantity();
        }).min(Integer::compare).orElse(0);
    }

    public String getName() {
        return this.name;
    }

    public Type getType() {
        return this.type;
    }

    public int getParentId() {
        return this.parent;
    }

    public Buildable getBuildable() {
        return this.furniture;
    }

    public Stream<ItemStack> getRequiredItems() {
        return Arrays.stream(this.items);
    }

    public int getRequiredItemSpace() {
        return this.getRequiredItems().map(i -> i.getMaterial().isStackable() ? 1 : i.getQuantity()).reduce(0, Integer::sum);
    }

}
