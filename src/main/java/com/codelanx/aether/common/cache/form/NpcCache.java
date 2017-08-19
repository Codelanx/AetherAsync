package com.codelanx.aether.common.cache.form;

import com.codelanx.aether.common.cache.query.ObjectInquiry;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.queries.NpcQueryBuilder;
import com.runemate.game.api.hybrid.region.Npcs;

import java.util.function.Supplier;

/**
 * Created by rogue on 8/14/2017.
 */
public class NpcCache extends LocatableCache<Npc> {

    public NpcCache() {
        super(null);
    }

    @Override
    public Supplier<NpcQueryBuilder> getBiasedQuery(ObjectInquiry inquiry) {
        return () -> {
            NpcQueryBuilder build = this.getRawQuery().get();
            build.names(inquiry.getTarget().getName());
            return build;
        };
    }

    @Override
    public Supplier<NpcQueryBuilder> getRawQuery() {
        return Npcs::newQuery;
    }
}
