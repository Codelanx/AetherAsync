package com.codelanx.aether.common.cache.form;

import com.codelanx.aether.common.cache.QueryType;
import com.codelanx.aether.common.cache.query.LocatableInquiry;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.queries.NpcQueryBuilder;
import com.runemate.game.api.hybrid.region.Npcs;

import java.util.function.Supplier;

/**
 * Created by rogue on 8/14/2017.
 */
public class NpcCache extends LocatableCache<Npc, LocatableInquiry> {

    public NpcCache() {
        super(Npcs::newQuery);
    }

    @Override
    public Supplier<NpcQueryBuilder> getBiasedQuery(LocatableInquiry inquiry) {
        return () -> {
            NpcQueryBuilder build = this.getRawQuery().get();
            build.names(inquiry.getTarget().getName());
            return build;
        };
    }

    @Override
    public Supplier<NpcQueryBuilder> getRawQuery() {
        return (Supplier<NpcQueryBuilder>) super.getRawQuery();
    }

    @Override
    public QueryType getType() {
        return QueryType.NPC;
    }

    @Override
    public long getLifetimeMS() {
        return Long.MAX_VALUE;
    }
}
