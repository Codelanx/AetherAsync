package com.codelanx.aether.common.json.locatable;

import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.cache.GameCache;
import com.codelanx.aether.common.cache.form.GameObjectCache;
import com.codelanx.aether.common.cache.query.ObjectInquiry;
import com.runemate.game.api.hybrid.entities.GameObject;

public interface GameObjectRef extends Findable<GameObject, ObjectInquiry> {

    public GameObject.Type getType();

    public int getId();

    @Override
    default public ObjectInquiry toUncachedInquiry() {
        return new ObjectInquiry(this);
    }

    @Override
    default GameObjectCache getGlobalCache() {
        return Caches.forGameObject();
    }
}
