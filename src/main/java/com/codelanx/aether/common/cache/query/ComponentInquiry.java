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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComponentInquiry that = (ComponentInquiry) o;

        if (getContainer() != that.getContainer()) return false;
        if (!getName().equals(that.getName())) return false;
        return getType() == that.getType();
    }

    @Override
    public int hashCode() {
        int result = getContainer();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getType().hashCode();
        return result;
    }
}
