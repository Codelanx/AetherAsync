package com.codelanx.aether.common.cache.form;

import com.codelanx.aether.common.cache.QueryType;
import com.codelanx.aether.common.cache.query.ObjectInquiry;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.queries.GameObjectQueryBuilder;
import com.runemate.game.api.hybrid.region.GameObjects;

import java.util.function.Supplier;

/**
 * Created by rogue on 8/14/2017.
 */
public class GameObjectCache extends LocatableCache<GameObject, ObjectInquiry> {
    
    public GameObjectCache() {
        super(GameObjects::newQuery);
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
        return (Supplier<GameObjectQueryBuilder>) super.getRawQuery();
    }

    @Override
    public QueryType getType() {
        return QueryType.GAME_OBJECT;
    }
}
