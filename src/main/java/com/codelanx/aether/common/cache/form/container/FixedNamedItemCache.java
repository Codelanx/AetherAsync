package com.codelanx.aether.common.cache.form.container;

import com.codelanx.aether.common.cache.form.container.FixedNamedItemCache.NamedSlot;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;

public class FixedNamedItemCache<E extends NamedSlot> extends FixedSizeItemCache {

    public FixedNamedItemCache(int size) {
        super(size);
    }

    public SpriteItem get(E slot) {
        if (slot.ordinal() > this.getCapacity()) {
            throw new UnsupportedOperationException("This slot is not supported in this gamemode (slot: " + slot.name() + ", game: " + this.getGameMode() + ")");
        }
        return this.get(slot.ordinal());
    }

    private String getGameMode() {
        return Environment.isRS3() ? "RS3" : Environment.isOSRS() ? "OSRS" : "Unknown";
    }

    public interface NamedSlot {

        public int ordinal();

        public String name();

    }

}
