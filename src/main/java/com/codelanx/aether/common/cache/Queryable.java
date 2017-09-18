package com.codelanx.aether.common.cache;

import com.codelanx.aether.common.cache.query.Inquiry;
import com.runemate.game.api.hybrid.entities.details.Interactable;

import java.util.stream.Stream;

public interface Queryable<T extends Interactable, R extends Inquiry> {

    public R toInquiry();

    public R toUncachedInquiry(); //this is more for inheritance than anything

    public GameCache<T, R> getGlobalCache();

    default public Stream<T> queryGlobal() {
        return this.getGlobalCache().get(this);
    }

}
