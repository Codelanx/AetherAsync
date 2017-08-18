package com.codelanx.aether.common;

import com.codelanx.aether.common.cache.GameCache;
import com.codelanx.aether.common.cache.Queryable;
import com.codelanx.aether.common.cache.query.ObjectInquiry;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.GameObject.Type;
import com.runemate.game.api.hybrid.entities.details.Interactable;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.util.Validatable;

import java.util.stream.Stream;

public interface Identifiable extends Queryable<ObjectInquiry> {

    public int getId();

    public String getName();

    public Type getType(); //TODO: fuck we'll have to abstract this won't we

    default public LocatableEntityQueryResults<GameObject> queryGameObjects() {
        return GameObjects.newQuery().types(this.getType()).names(this.getName()).results();
    }

    @Override
    default public ObjectInquiry toInquiry() {
        return new ObjectInquiry(this);
    }

    default public <R extends Validatable & Interactable, G extends GameCache<? extends R, ? super ObjectInquiry>> boolean isPresent(G cache) {
        ObjectInquiry inq = this.toInquiry();
        return fromInquiry(inq, cache).anyMatch(Validatable::isValid);

    }

    public static <R extends Validatable & Interactable, I extends ObjectInquiry, G extends GameCache<? extends R, ? super I>> Stream<? extends R> fromInquiry(I inq, G cache) {
        return cache.get(inq).filter(Validatable::isValid);
    }
}
