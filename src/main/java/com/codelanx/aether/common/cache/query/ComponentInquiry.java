package com.codelanx.aether.common.cache.query;

import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceComponent.Type;

public class ComponentInquiry extends Inquiry {

    private final int container;
    private final String name;
    private final Type type;

    public ComponentInquiry(int container, String name, Type type) {
        this.container = container;
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public int getContainer() {
        return this.container;
    }

    public Type getType() {
        return this.type;
    }
}
