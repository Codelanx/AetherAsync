package com.codelanx.aether.common.cache.form;

import com.codelanx.aether.common.cache.query.ObjectInquiry;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.queries.PlayerQueryBuilder;
import com.runemate.game.api.hybrid.region.Players;

import java.util.function.Supplier;

/**
 * Created by rogue on 8/14/2017.
 */
public class PlayerCache extends LocatableCache<Player> {
    
    public PlayerCache() {
        super(null);
    }

    @Override
    public Supplier<PlayerQueryBuilder> getBiasedQuery(ObjectInquiry inquiry) {
        PlayerQueryBuilder build = this.getRawQuery().get();
        build.names(inquiry.getTarget().getName());
        return () -> build;
    }

    @Override
    public Supplier<PlayerQueryBuilder> getRawQuery() {
        return Players::newQuery;
    }
}
