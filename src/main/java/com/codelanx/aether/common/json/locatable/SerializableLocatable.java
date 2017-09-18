package com.codelanx.aether.common.json.locatable;

import com.codelanx.aether.common.cache.query.LocatableInquiry;
import com.codelanx.commons.data.FileSerializable;
import com.runemate.game.api.hybrid.entities.details.Interactable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

//made it abstract mostly because we don't want instantiation directly
public abstract class SerializableLocatable<T extends Interactable, I extends LocatableInquiry> implements Findable<T, I>, FileSerializable {

    private final String name;
    private I inq; //non-volatile, results will likely be the same and the lack of thread caching would be a worse tradeoff

    protected SerializableLocatable(String name) {
        this.name = name;
    }

    protected SerializableLocatable(Map<String, Object> data) {
        this.name = Optional.ofNullable((String) data.get("name")).orElseThrow(() -> new IllegalArgumentException("Missing name for serialized locatable"));
    }

    protected SerializableLocatable(Findable<T, I> other) {
        this.name = other.getName();
    }


    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> back = new LinkedHashMap<>();
        back.put("name", this.name);
        return back;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public I toInquiry() {
        if (this.inq == null) {
            I back = this.toUncachedInquiry();
            if (this.inq == null) {
                this.inq = back;
            }
        }
        return this.inq;
    }

    @Override
    public abstract I toUncachedInquiry();
}
