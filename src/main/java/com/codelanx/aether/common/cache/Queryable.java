package com.codelanx.aether.common.cache;

import com.codelanx.aether.common.cache.query.Inquiry;
import com.codelanx.commons.util.Parallel;
import com.runemate.game.api.hybrid.entities.details.Interactable;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public interface Queryable<T extends Interactable, R extends Inquiry> {

    public AtomicReference<R> getReferenceToInquiry();

    default public R toInquiry() {
        return Parallel.doubleLockInit(this.getReferenceToInquiry(), this::toUncachedInquiry);
    }

    public R toUncachedInquiry(); //this is more for inheritance than anything

    public GameCache<T, R> getGlobalCache();

    default public Stream<T> queryGlobal() {
        return this.getGlobalCache().get(this);
    }

}
