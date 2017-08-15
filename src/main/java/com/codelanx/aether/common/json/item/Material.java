package com.codelanx.aether.common.json.item;

import com.codelanx.aether.common.cache.query.MaterialInquiry;

public interface Material {

    public int getId();

    public String getName();

    public String getPlural();

    public boolean isStackable();

    default public boolean isEquippable() {
        return false;
    }
    
    default public MaterialInquiry toInquiry() {
        return new MaterialInquiry(this);
    }
}
