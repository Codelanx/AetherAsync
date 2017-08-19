package com.codelanx.aether.common.cache.form;

import com.codelanx.aether.common.cache.query.ObjectInquiry;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.queries.GameObjectQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.QueryResults;
import com.runemate.game.api.hybrid.region.GameObjects;

import java.util.function.Supplier;

/**
 * Created by rogue on 8/14/2017.
 */
public class GameObjectCache extends LocatableCache<GameObject> {
    
    public GameObjectCache() {
        super(null);
    }

    @Override
    public Supplier<? extends QueryResults<GameObject, ?>> getResults(ObjectInquiry inquiry) {
        return super.getResults(inquiry);
    }

    @Override
    public Supplier<GameObjectQueryBuilder> getBiasedQuery(ObjectInquiry inquiry) {
        return () -> {
            GameObjectQueryBuilder build = this.getRawQuery().get();
            build.names(inquiry.getTarget().getName());
            build.types(inquiry.getTarget().getType());
            return build;
        };
    }

    @Override
    public Supplier<GameObjectQueryBuilder> getRawQuery() {
        return GameObjects::newQuery;
    }
}
