package com.codelanx.aether.common.cache;

import com.codelanx.aether.common.cache.form.*;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.Equipment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Interfaces;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.GroundItems;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by rogue on 8/14/2017.
 */
public enum QueryType {
    
    BANK(Bank.class, me -> new ContainerCache(Bank::newQuery, me)),
    INVENTORY(Inventory.class, me -> new ContainerCache(Inventory::newQuery, me)),
    EQUIPMENT(Equipment.class, me -> new ContainerCache(Equipment::newQuery, me)),
    NPC(Npcs.class, NpcCache::new),
    GAME_OBJECT(GameObjects.class, GameObjectCache::new),
    PLAYER(Npcs.class, PlayerCache::new),
    COMPONENT(Interfaces.class, InterfaceCache::new),
    GROUND_ITEMS(GroundItems.class, GroundItemCache::new),
    ;
    
    private final Class<?> token;
    private final GameCache<?, ?> cache;

    private QueryType(Class<?> token, Supplier<GameCache<?, ?>> cache) {
        this(token, me -> cache.get());
    }
    
    private QueryType(Class<?> token, Function<QueryType, GameCache<?, ?>> cache) {
        this.token = token;
        this.cache = cache.apply(this);
    }
    
    public GameCache<?, ?> getCache() {
        return this.cache;
    }
    
    public Class<?> getToken() {
        return this.token;
    }
}
