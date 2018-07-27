package com.codelanx.aether.common.cache.form;

import com.codelanx.aether.common.cache.GameCache;
import com.codelanx.aether.common.cache.query.LocatableInquiry;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.queries.LocatableEntityQueryBuilder;
import com.runemate.game.api.hybrid.queries.QueryBuilder;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import java.util.function.Supplier;

/**
 * Created by rogue on 8/14/2017.
 */
public abstract class LocatableCache<E extends LocatableEntity, I extends LocatableInquiry> extends GameCache<E, I> {
    
    private final Supplier<LocatableEntityQueryBuilder<E, ?>> target;

    public LocatableCache(Supplier<LocatableEntityQueryBuilder<E, ?>> target) {
        this.target = target;
    }

    @Override
    public Supplier<? extends LocatableEntityQueryResults<E>> getRunemateResults(I inquiry) {
        return this.getBiasedQuery(inquiry).get()::results;
    }

    public abstract <R extends LocatableEntityQueryBuilder<E, R>> Supplier<R> getBiasedQuery(I inquiry);

    @Override
    public Supplier<? extends QueryBuilder<E, ?, ?>> getRawQuery() {
        return this.target;
    }
}
