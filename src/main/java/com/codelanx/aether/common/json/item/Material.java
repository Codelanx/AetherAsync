package com.codelanx.aether.common.json.item;

import com.codelanx.aether.common.cache.GameCache;
import com.codelanx.aether.common.cache.Queryable;
import com.codelanx.aether.common.cache.query.MaterialInquiry;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;

import java.util.stream.Stream;

public interface Material extends Queryable<SpriteItem, MaterialInquiry> {

    public int getId();

    public String getName();

    public boolean isStackable();

    default public boolean isEquippable() {
        return false;
    }

    @Override
    default public MaterialInquiry toInquiry() {
        return new MaterialInquiry(this);
    }

    @Override
    default public GameCache<SpriteItem, MaterialInquiry> getGlobalCache() {
        throw new UnsupportedOperationException("Material is used in multiple caches, you must use Caches#for*");
    }

    @Override
    default Stream<SpriteItem> queryGlobal() {
        throw new UnsupportedOperationException("Material is used in multiple caches, you must use Caches#for*");
    }

    @Override
    default public MaterialInquiry toUncachedInquiry() {
        return new MaterialInquiry(this);
    }
}
