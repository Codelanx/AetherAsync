package com.codelanx.aether.fletching;

import com.codelanx.aether.common.item.Material;

public enum FletchingMaterial implements Material {

    UNFINISHED_ARROW(-1, ""),
    STEEL_ARROWTIPS(-1, ""),
    ;
    private final int id;
    private final String name;
    private final String plural;

    private FletchingMaterial(int id, String name) {
        this(id, name, name);
    }

    private FletchingMaterial(int id, String name, String plural) {
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
