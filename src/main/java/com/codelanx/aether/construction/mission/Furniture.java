package com.codelanx.aether.construction.mission;

import com.runemate.game.api.hybrid.entities.GameObject.Type;

public enum Furniture implements Destructable {
    LARDER(13566, "Larder", Type.PRIMARY),
    ;

    private final int id;
    private final String name;
    private final Type type;

    private Furniture(int id, String name, Type type) {
        this.id = id;
        this.name = name;
        this.type = type;
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
}
