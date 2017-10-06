package com.codelanx.aether.common.cache.query;

import com.codelanx.aether.common.json.entity.Entity;
import com.runemate.game.api.hybrid.entities.details.Interactable;

//while the queries themselves aren't strictly related to location, the query results are
//this is to aid in api design, e.g. Queryable<? extends LocatableInquiry>
public class LocatableInquiry extends Inquiry {

    private final Entity<? extends Interactable, ? extends LocatableInquiry> target;

    public LocatableInquiry(Entity<? extends Interactable, ? extends LocatableInquiry> target) {
        this.target = target;
    }

    public Entity<? extends Interactable, ? extends LocatableInquiry> getTarget() {
        return this.target;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocatableInquiry inquiry = (LocatableInquiry) o;

        return getTarget().equals(inquiry.getTarget());
    }

    @Override
    public int hashCode() {
        return getTarget().hashCode();
    }
}
