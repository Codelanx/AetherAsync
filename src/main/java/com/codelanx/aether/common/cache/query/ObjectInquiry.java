package com.codelanx.aether.common.cache.query;

import com.codelanx.aether.common.Identifiable;

/**
 * Created by rogue on 8/14/2017.
 */
public class ObjectInquiry extends Inquiry {
    
    private final Identifiable target;
    
    public ObjectInquiry(Identifiable target) {
        this.target = target;
    }
    
    public Identifiable getTarget() {
        return this.target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectInquiry inquiry = (ObjectInquiry) o;

        return getTarget().equals(inquiry.getTarget());
    }

    @Override
    public int hashCode() {
        return getTarget().hashCode();
    }
}
