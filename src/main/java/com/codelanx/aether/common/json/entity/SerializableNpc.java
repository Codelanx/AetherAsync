package com.codelanx.aether.common.json.entity;

import com.codelanx.aether.common.cache.query.LocatableInquiry;
import com.runemate.game.api.hybrid.entities.Npc;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class SerializableNpc extends SerializableEntity<Npc, LocatableInquiry> implements NpcRef {

    private final AtomicReference<LocatableInquiry> inq = new AtomicReference<>();

    protected SerializableNpc(String name) {
        super(name);
    }

    protected SerializableNpc(Map<String, Object> data) {
        super(data);
    }

    public SerializableNpc(NpcRef ref) {
        super(ref);
    }

    @Override
    public AtomicReference<LocatableInquiry> getReferenceToInquiry() {
        return this.inq;
    }

    @Override
    public LocatableInquiry toUncachedInquiry() {
        return NpcRef.super.toUncachedInquiry();
    }
}
