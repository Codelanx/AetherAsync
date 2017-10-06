package com.codelanx.aether.common.json.entity;

import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.cache.form.GroundItemCache;
import com.codelanx.aether.common.cache.query.LocatableInquiry;
import com.runemate.game.api.hybrid.entities.GroundItem;

public interface GroundItemRef extends Entity<GroundItem, LocatableInquiry> {

    @Override
    default public LocatableInquiry toUncachedInquiry() {
        return new LocatableInquiry(this);
    }

    @Override
    default GroundItemCache getGlobalCache() {
        return Caches.forGroundItems();
    }
}
