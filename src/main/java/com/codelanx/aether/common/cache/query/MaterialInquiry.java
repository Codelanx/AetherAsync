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

}
