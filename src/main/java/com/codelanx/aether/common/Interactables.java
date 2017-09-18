package com.codelanx.aether.common;

import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.cache.GameCache;
import com.codelanx.aether.common.cache.form.GameObjectCache;
import com.codelanx.aether.common.cache.query.ObjectInquiry;
import com.codelanx.aether.common.json.locatable.GameObjectRef;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.GameObject.Type;

public enum Interactables implements GameObjectRef {
    RANGE(26181, "Range", Type.PRIMARY),
    FURNACE(-1, "Furnace", Type.PRIMARY),
    ;

    private final int id;
    private final String name;
    private final Type type;
    private volatile ObjectInquiry inquiry;

    private Interactables(int id, String name, Type type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public ObjectInquiry toInquiry() {
        if (this.inquiry == null) {
            ObjectInquiry inquiry = this.toUncachedInquiry();
            if (this.inquiry == null) {
                this.inquiry = inquiry;
            }
        }
        return this.inquiry;
    }

    @Override
    public ObjectInquiry toUncachedInquiry() {
        return new ObjectInquiry(this);
    }

    @Override
    public GameObjectCache getGlobalCache() {
        return Caches.forGameObject();
    }
}
