package com.codelanx.aether.common.cache.form;

import com.codelanx.aether.common.cache.GameCache;
import com.codelanx.aether.common.cache.QueryType;
import com.codelanx.aether.common.cache.query.LocatableInquiry;
import com.codelanx.aether.common.json.entity.Entity;
import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.queries.GroundItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.GroundItems;

import java.util.function.Supplier;

public class GroundItemCache extends GameCache<GroundItem, LocatableInquiry> {

    @Override
    public Supplier<LocatableEntityQueryResults<GroundItem>> getResults(LocatableInquiry inquiry) {
        return () -> {
            GroundItemQueryBuilder query = this.getRawQuery().get();
            Entity target = inquiry.getTarget();
            query.names(target.getName());
            //TODO: this needs abstraction in terms of different locatable queries
            return query.results();
        };
    }

    @Override
    public Supplier<GroundItemQueryBuilder> getRawQuery() {
        return GroundItems::newQuery;
    }

    @Override
    public QueryType getType() {
        return QueryType.GROUND_ITEMS;
    }
}
