package com.codelanx.aether.construction;

import com.codelanx.aether.common.Identifiable;
import com.runemate.game.api.hybrid.entities.GameObject.Type;

public enum Interactables implements Identifiable {
    PORTAL(15482, "Portal", Type.PRIMARY),
    ;

    private final int id;
    private final String name;
    private final Type type;

    private Interactables(int id, String name, Type type) {
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
