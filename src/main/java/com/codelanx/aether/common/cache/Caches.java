package com.codelanx.aether.common.cache;

import com.codelanx.aether.common.cache.form.ContainerCache;
import com.codelanx.aether.common.cache.form.GameObjectCache;
import com.codelanx.aether.common.cache.form.GroundItemCache;
import com.codelanx.aether.common.cache.form.InterfaceCache;
import com.codelanx.aether.common.cache.form.NpcCache;
import com.codelanx.aether.common.cache.form.PlayerCache;

import java.util.Arrays;

/**
 * Created by rogue on 8/14/2017.
 */
public final class Caches {
    
    private Caches() {
        
    }

    public static void invalidateAll() {
        Arrays.stream(QueryType.values()).map(QueryType::getCache).forEach(GameCache::invalidateAll);
    }
    
    public static NpcCache forNpc() {
        return (NpcCache) QueryType.NPC.getCache();
    }
    
    public static GameObjectCache forGameObject() {
        return (GameObjectCache) QueryType.GAME_OBJECT.getCache();
    }
    
    public static PlayerCache forPlayer() {
        return (PlayerCache) QueryType.GAME_OBJECT.getCache();
    }

    public static ContainerCache forBank() {
        return (ContainerCache) QueryType.BANK.getCache();
    }

    public static ContainerCache forInventory() {
        return (ContainerCache) QueryType.INVENTORY.getCache();
    }

    public static InterfaceCache forInterfaces() {
        return (InterfaceCache) QueryType.COMPONENT.getCache();
    }

    public static GroundItemCache forGroundItems() {
        return (GroundItemCache) QueryType.GROUND_ITEMS.getCache();
    }

    public static ContainerCache forEquipment() {
        return (ContainerCache) QueryType.EQUIPMENT.getCache();
    }

}
