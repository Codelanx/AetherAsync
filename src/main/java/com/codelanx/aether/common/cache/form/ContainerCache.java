package com.codelanx.aether.common.cache.form;

import com.codelanx.aether.common.cache.GameCache;
import com.codelanx.aether.common.cache.query.MaterialInquiry;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;

import java.util.function.Supplier;

/**
 * Created by rogue on 8/14/2017.
 */
public class ContainerCache extends GameCache<SpriteItem, MaterialInquiry> {
    
    private final Supplier<SpriteItemQueryBuilder> target;
    
    public ContainerCache(Supplier<SpriteItemQueryBuilder> target) {
        this.target = target;
    }
    
    @Override
    public Supplier<SpriteItemQueryResults> getResults(MaterialInquiry inquiry) {
        SpriteItemQueryBuilder query = this.getRawQuery().get();
        query.ids(inquiry.getMaterial().getId());
        query.names(inquiry.getMaterial().getName());
        query.equipable(inquiry.getMaterial().isEquippable());
        query.stacks(inquiry.getMaterial().isStackable());
        return query::results;
    }

    @Override
    public Supplier<SpriteItemQueryBuilder> getRawQuery() {
        return this.target;
    }
}
