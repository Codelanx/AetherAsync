package com.codelanx.aether.common.json.entity;

import com.codelanx.aether.common.cache.query.LocatableInquiry;
import com.runemate.game.api.hybrid.entities.GroundItem;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class SerializableGroundItem extends SerializableEntity<GroundItem, LocatableInquiry> implements GroundItemRef {

    private final AtomicReference<LocatableInquiry> inq = new AtomicReference<>();

    protected SerializableGroundItem(String name) {
        super(name);
    }

    protected SerializableGroundItem(Map<String, Object> data) {
        super(data);
    }

    public SerializableGroundItem(GroundItemRef ref) {
        super(ref);
    }

    @Override
    public AtomicReference<LocatableInquiry> getReferenceToInquiry() {
        return this.inq;
    }

    @Override
    public LocatableInquiry toUncachedInquiry() {
        return GroundItemRef.super.toUncachedInquiry();
    }
}
