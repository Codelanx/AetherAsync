package com.codelanx.aether.common.json.locatable;

import com.codelanx.aether.common.cache.Queryable;
import com.codelanx.aether.common.cache.query.LocatableInquiry;
import com.runemate.game.api.hybrid.entities.details.Interactable;

public interface Findable<T extends Interactable, E extends LocatableInquiry> extends Queryable<T, E> { //we really won't be using this much, just its subclasses

    public String getName();
}
