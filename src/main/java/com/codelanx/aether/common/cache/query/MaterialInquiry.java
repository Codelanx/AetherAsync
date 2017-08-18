package com.codelanx.aether.common.cache.query;

import com.codelanx.aether.common.json.item.Material;

/**
 * Created by rogue on 8/14/2017.
 */
public class MaterialInquiry extends Inquiry {
    
    private final Material material;
    
    public MaterialInquiry(Material material) {
        this.material = material;
    }
    
    public Material getMaterial() {
        return this.material;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MaterialInquiry that = (MaterialInquiry) o;

        return getMaterial().equals(that.getMaterial());
    }

    @Override
    public int hashCode() {
        return getMaterial().hashCode();
    }

    @Override
    public String toString() {
        return "MaterialInquiry{" +
                "material=" + material +
                '}';
    }
}
