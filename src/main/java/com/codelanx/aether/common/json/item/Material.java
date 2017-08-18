package com.codelanx.aether.common.json.item;

import com.codelanx.aether.common.cache.Queryable;
import com.codelanx.aether.common.cache.query.MaterialInquiry;

public interface Material extends Queryable<MaterialInquiry> {

    public int getId();

    public String getName();

    public String getPlural();

    public boolean isStackable();

    default public boolean isEquippable() {
        return false;
    }

    @Override
    default public MaterialInquiry toInquiry() {
        return new MaterialInquiry(this);
    }
}
