package com.codelanx.aether.crafting;

import com.codelanx.aether.common.item.Material;

public enum CraftingMaterial implements Material {

    GOLD_BAR(-1, "Gold bar"),
    BRACELET_MOULD(-1, "Bracelet mould"),
    GOLD_BRACELET(-1, "Gold bracelet"),
    ;
    private final int id;
    private final String name;
    private final String plural;

    private CraftingMaterial(int id, String name) {
        this(id, name, name);
    }

    private CraftingMaterial(int id, String name, String plural) {
        this.id = id;
        this.name = name;
        this.plural = plural;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPlural() {
        return this.plural;
    }

    @Override
    public boolean isStackable() {
        return false;
    }
}
