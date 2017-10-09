package com.codelanx.aether.common.cache.form;

import com.codelanx.aether.common.cache.QueryType;
import com.codelanx.aether.common.cache.query.LocatableInquiry;
import com.codelanx.aether.common.cache.query.ObjectInquiry;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.queries.LocatableEntityQueryBuilder;
import com.runemate.game.api.hybrid.queries.PlayerQueryBuilder;
import com.runemate.game.api.hybrid.region.Players;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Created by rogue on 8/14/2017.
 */
public class PlayerCache extends LocatableCache<Player, LocatableInquiry> {
    
    public PlayerCache() {
        super(Players::newQuery);
    }

    @Override
    public Supplier<PlayerQueryBuilder> getBiasedQuery(LocatableInquiry inquiry) {
        return () -> {
            PlayerQueryBuilder build = this.getRawQuery().get();
            build.names(inquiry.getTarget().getName());
            return build;
        };
    }

    @Override
    public Supplier<PlayerQueryBuilder> getRawQuery() {
        return (Supplier<PlayerQueryBuilder>) super.getRawQuery();
    }

    @Override
    public QueryType getType() {
        return QueryType.PLAYER;
    }

    @Override
    public long getLifetimeMS() {
        return TimeUnit.SECONDS.toMillis(5);
    }
}
