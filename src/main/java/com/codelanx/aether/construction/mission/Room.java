package com.codelanx.aether.construction.mission;

import com.runemate.game.api.hybrid.entities.GameObject.Type;

//TODO
public enum Room implements Buildable {

    KITCHEN,
    ;

    @Override
    public Destructable getResult() {
        return null;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Type getType() {
        return null;
    }
}
