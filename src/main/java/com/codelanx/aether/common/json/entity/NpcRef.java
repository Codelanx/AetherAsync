package com.codelanx.aether.common.json.entity;

import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.cache.form.NpcCache;
import com.codelanx.aether.common.cache.query.LocatableInquiry;
import com.runemate.game.api.hybrid.entities.Npc;

public interface NpcRef extends Entity<Npc, LocatableInquiry> {

    @Override
    default public NpcCache getGlobalCache() {
        return Caches.forNpc();
    }

    @Override
    default public LocatableInquiry toUncachedInquiry() {
        return new LocatableInquiry(this);
    }
}
