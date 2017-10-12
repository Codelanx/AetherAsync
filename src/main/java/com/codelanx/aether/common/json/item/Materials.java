package com.codelanx.aether.common.json.item;

import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.cache.query.LocatableInquiry;
import com.codelanx.aether.common.cache.query.MaterialInquiry;
import com.codelanx.commons.util.Parallel;
import com.runemate.game.api.hybrid.entities.definitions.ItemDefinition;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

//common enum for items, should stick to basics here and keep skill-specific items in a different enum
//this is for a lower memory footprint, but this will likely be externalized in the future anyhow
public enum Materials implements Material {
    COINS(995, "Coins"),
    KNIFE(946, "Knife"),
    ;

    private final int id;
    private final String name;
    private final AtomicReference<MaterialInquiry> inq = new AtomicReference<>();
    private final AtomicReference<LocatableInquiry> groundItem = new AtomicReference<>();

    private Materials(int id, String name) {
        this.id = id;
        this.name = name;
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
    public boolean isStackable() {
        switch (this) {
            case COINS:
                return true;
            default:
                return false;
        }
    }

    public static Material getMaterial(SpriteItem item) {
        if (item == null) {
            return null;
        }
        return Aether.getBot().getData().fromSpriteItem(item);
    }

    public static Material getMaterial(ItemDefinition definition) {
        if (definition == null) {
            return null;
        }
        return Aether.getBot().getData().fromDefinition(definition);
    }

    @Override
    public AtomicReference<LocatableInquiry> getReferenceGroundItemInquiry() {
        return this.groundItem;
    }

    @Override
    public String toString() {
        return "Materials#" + this.name() + "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public AtomicReference<MaterialInquiry> getReferenceToInquiry() {
        return this.inq;
    }
}
