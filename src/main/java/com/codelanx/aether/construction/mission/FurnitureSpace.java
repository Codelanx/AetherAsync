package com.codelanx.aether.construction.mission;


import com.runemate.game.api.hybrid.entities.GameObject.Type;

public enum FurnitureSpace implements Buildable {
    LARDER(15403, "Larder space", Type.PRIMARY, Furniture.LARDER),
    ;

    private final int id;
    private final String name;
    private final Type type;
    private final Furniture destructable;

    private FurnitureSpace(int id, String name, Type type, Furniture destructable) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.destructable = destructable;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public Furniture getResult() {
        return this.destructable;
    }
}
