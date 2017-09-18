package com.codelanx.aether.common.cache.query;

import com.codelanx.aether.common.json.locatable.GameObjectRef;

/**
 * Created by rogue on 8/14/2017.
 */
public class ObjectInquiry extends LocatableInquiry {
    
    public ObjectInquiry(GameObjectRef target) {
        super(target);
    }
    
    public GameObjectRef getTarget() {
        return (GameObjectRef) super.getTarget();
    }

}
