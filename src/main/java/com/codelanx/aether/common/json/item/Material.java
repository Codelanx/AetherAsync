package com.codelanx.aether.common.json.item;

import com.codelanx.aether.common.cache.GameCache;
import com.codelanx.aether.common.cache.Queryable;
import com.codelanx.aether.common.cache.query.LocatableInquiry;
import com.codelanx.aether.common.cache.query.MaterialInquiry;
import com.codelanx.aether.common.json.entity.GroundItemRef;
import com.codelanx.commons.util.Parallel;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public interface Material extends Queryable<SpriteItem, MaterialInquiry> {

    public static Material EMPTY = new SerializableMaterial("", -1, false, false);

    public int getId();

    public String getName();

    public boolean isStackable();

    default public boolean isEquippable() {
        return false;
    }

    //these are basically purposefully long to discourage use, they're more for internal mapping
    public AtomicReference<LocatableInquiry> getReferenceGroundItemInquiry();

    @Override
    default public MaterialInquiry toInquiry() {
        return Parallel.doubleLockInit(this.getReferenceToInquiry(), this::toUncachedInquiry);
    }

    default public LocatableInquiry toGroundItemInquiry() {
        return Parallel.doubleLockInit(this.getReferenceGroundItemInquiry(), () -> {
            GroundItemRef e = new GroundItemRef() {

                @Override
                public AtomicReference<LocatableInquiry> getReferenceToInquiry() {
                    return Material.this.getReferenceGroundItemInquiry();
                }

                @Override
                public String getName() {
                    return Material.this.getName();
                }
            };
            return e.toInquiry();
        });
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
