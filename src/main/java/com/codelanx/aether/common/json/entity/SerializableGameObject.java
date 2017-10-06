package com.codelanx.aether.common.json.entity;

import com.codelanx.aether.common.cache.query.ObjectInquiry;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.GameObject.Type;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class SerializableGameObject extends SerializableEntity<GameObject, ObjectInquiry> implements GameObjectRef {

    private final int id;
    private final Type type;
    private final AtomicReference<ObjectInquiry> inq = new AtomicReference<>();

    public SerializableGameObject(String name, int id, Type type) {
        super(name);
        this.id = id;
        this.type = type;
    }

    protected SerializableGameObject(Map<String, Object> data) {
        super(data);
        this.id = Optional.ofNullable((Long) data.get("container")).map(Long::intValue).orElseThrow(() -> new IllegalArgumentException("Bad id for game object"));
        this.type = Optional.ofNullable((String) data.get("type")).map(String::toUpperCase).map(GameObject.Type::valueOf).orElse(Type.PRIMARY);
    }

    public SerializableGameObject(GameObjectRef ref) {
        super(ref);
        this.id = ref.getId();
        this.type = ref.getType();
    }

    @Override
    public AtomicReference<ObjectInquiry> getReferenceToInquiry() {
        return this.inq;
    }

    @Override
    public ObjectInquiry toUncachedInquiry() {
        return GameObjectRef.super.toUncachedInquiry();
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public int getId() {
        return this.id;
    }

}
