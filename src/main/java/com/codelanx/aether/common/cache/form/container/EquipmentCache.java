package com.codelanx.aether.common.cache.form.container;

import com.codelanx.aether.common.cache.QueryType;
import com.codelanx.aether.common.cache.form.container.EquipmentCache.EquipSlot;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;

import java.util.function.Supplier;

//this'll be query-once and updated/invalidated based on equipable (RS3) or inventory changes (OSRS)
public class EquipmentCache extends FixedNamedItemCache<EquipSlot> {

    public EquipmentCache() {
        super(calculateSize());
    }

    private static int calculateSize() {
        return EquipSlot.values().length - (Environment.isRS3() ? 2 : 0);
    }

    public enum EquipSlot implements NamedSlot {
        HEAD,
        CAPE,
        NECK,
        WEAPON,
        BODY,
        SHIELD,
        LEGS,
        HANDS,
        FEET,
        RING,
        AMMO,
        ;
    }

    @Override
    public QueryType getType() {
        return QueryType.EQUIPMENT;
    }

    @Override
    public Supplier<? extends SpriteItemQueryBuilder> getRawQuery() {
        return Equipment::newQuery;
    }

    @Override
    public long getLifetimeMS() {
        return 0;
    }

}
