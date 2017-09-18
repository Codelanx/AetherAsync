package com.codelanx.aether.bots.smithing;

import com.codelanx.aether.common.cache.query.ObjectInquiry;
import com.codelanx.aether.common.json.locatable.GameObjectRef;
import com.runemate.game.api.hybrid.entities.GameObject.Type;

//TODO: jsonify
public enum BlastObject implements GameObjectRef {

    //everything we need for an automatic world
    BELT(9101, "Conveyer belt", Type.PRIMARY),
    SINK(9143, "Sink", Type.PRIMARY),
    COFFER(29330, "Coffer", Type.PRIMARY),
    BAR_DISPENSER(9092, "Bar dispenser", Type.PRIMARY),
    BANK_CHEST(26707, "Bank chest", Type.PRIMARY),
    PIPES(9116, "", Type.PRIMARY), //necessary, or are these only the fixed ones? let's skip fixing furnace for now
    ;

    private final int id;
    private final String name;
    private final Type type;
    private final ObjectInquiry inq;

    private BlastObject(int id, String name, Type type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.inq = this.toUncachedInquiry();
    }

    @Override
    public ObjectInquiry toInquiry() {
        return this.inq;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Type getType() {
        return this.type;
    }
}
