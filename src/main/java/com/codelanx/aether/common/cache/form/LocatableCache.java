package com.codelanx.aether.common.cache.form;

import com.codelanx.aether.common.cache.GameCache;
import com.codelanx.aether.common.cache.query.ObjectInquiry;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.queries.LocatableEntityQueryBuilder;
import com.runemate.game.api.hybrid.queries.QueryBuilder;
import com.runemate.game.api.hybrid.queries.results.QueryResults;

import java.util.function.Supplier;

/**
 * Created by rogue on 8/14/2017.
 */
public abstract class LocatableCache<E extends LocatableEntity> extends GameCache<E, ObjectInquiry> {
    
    private final Supplier<LocatableEntityQueryBuilder<E, ?>> target;

    public LocatableCache(Supplier<LocatableEntityQueryBuilder<E, ?>> target) {
        this.target = target;
    }

    @Override
    public Supplier<? extends QueryResults<E, ?>> getResults(ObjectInquiry inquiry) {
        return this.getBiasedQuery(inquiry).get()::results;
    }
    
    public abstract Supplier<? extends LocatableEntityQueryBuilder<E, ?>> getBiasedQuery(ObjectInquiry inquiry);

    @Override
    public Supplier<? extends QueryBuilder<E, ?, ?>> getRawQuery() {
        return this.target;
    }
}
