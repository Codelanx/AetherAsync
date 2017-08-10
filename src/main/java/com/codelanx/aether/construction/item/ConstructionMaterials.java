package com.codelanx.aether.construction.item;

import com.codelanx.aether.common.item.Material;

//an incomplete enum filled enough for my needs
public enum ConstructionMaterials implements Material {
    LAW_RUNE(563, "Law rune"),
    SAW(8794, "Saw"),
    HAMMER(2347, "Hammer"),
    MYSTIC_DUST_STAFF(20739, "Mystic Dust Staff"),
    OAK_PLANK(8778, "Oak plank", "Oak planks"),
    ;

    private final int id;
    private final String name;
    private final String plural;

    private ConstructionMaterials(int id, String name) {
        this(id, name, name);
    }

    private ConstructionMaterials(int id, String name, String plural) {
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
        return this == LAW_RUNE;
    }

    @Override
    public String toString() {
        return "ConstructionMaterials#" + this.name() + "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
