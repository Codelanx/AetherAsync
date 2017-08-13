package com.codelanx.aether.common.item;

public interface Material {

    public int getId();

    public String getName();

    public String getPlural();

    public boolean isStackable();

    default public boolean isEquippable() {
        return false;
    }
}
