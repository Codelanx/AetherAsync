package com.codelanx.aether.common.cache.form;

import com.codelanx.aether.common.cache.GameCache;
import com.codelanx.aether.common.cache.QueryType;
import com.codelanx.aether.common.cache.query.ComponentInquiry;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceComponent;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.hybrid.queries.InterfaceComponentQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.InterfaceComponentQueryResults;

import java.util.function.Supplier;

public class InterfaceCache extends GameCache<InterfaceComponent, ComponentInquiry> {

    @Override
    public Supplier<InterfaceComponentQueryResults> getResults(ComponentInquiry inquiry) {
        return () -> {
            InterfaceComponentQueryBuilder builder = this.getRawQuery().get();
            builder.types(inquiry.getType());
            builder.names(inquiry.getName());
            builder.containers(inquiry.getContainer());
            return builder.results();
        };
    }

    @Override
    public Supplier<InterfaceComponentQueryBuilder> getRawQuery() {
        return Interfaces::newQuery;
    }

    @Override
    public QueryType getType() {
        return QueryType.COMPONENT;
    }
}
