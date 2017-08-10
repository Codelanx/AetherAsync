package com.codelanx.aether.common;

import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.GameObject.Type;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.GameObjects;

public interface Identifiable {

    public int getId();

    public String getName();

    public Type getType(); //TODO: fuck we'll have to abstract this won't we

    public default LocatableEntityQueryResults<GameObject> queryGameObjects() {
        return GameObjects.newQuery().types(this.getType()).names(this.getName()).results();
    }
}
