package com.codelanx.aether.common;

import com.codelanx.aether.common.cache.query.ObjectInquiry;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.GameObject.Type;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.GameObjects;

public enum Interactables implements Identifiable {

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

    public LocatableEntityQueryResults<GameObject> query() {
        return GameObjects.newQuery().names(this.getName()).types(this.getType()).results();
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
            ObjectInquiry inquiry = new ObjectInquiry(this);
            if (this.inquiry == null) {
                this.inquiry = inquiry;
            }
        }
        return this.inquiry;
    }
}
