package com.codelanx.aether.common.cache.form.container;

import com.codelanx.commons.logging.Logging;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.script.framework.listeners.InventoryListener;
import com.runemate.game.api.script.framework.listeners.events.ItemEvent;

public class InventoryCache extends FixedSizeItemCache implements InventoryListener {

    public InventoryCache() {
        super(28);
    }

    @Override
    public void onItemAdded(ItemEvent event) {
        SpriteItem i = event.getItem();
        Logging.info(String.format("[InventoryCache] Item add event called: {index: %d, change: %d, name: %s}", i.getIndex(), event.getQuantityChange(), i.getDefinition().getName()));
        this.load(event.getItem());
    }

    @Override
    public void onItemRemoved(ItemEvent event) {
        SpriteItem i = event.getItem();
        int index = i.getIndex();
        Logging.info(String.format("[InventoryCache] Item remove event called: {index: %d, change: %d, name: %s}", i.getIndex(), event.getQuantityChange(), i.getDefinition().getName()));
        SpriteItem set = event.getItem().getQuantity() - event.getQuantityChange() <= 0
                ? null
                : event.getItem();
        this.set(index, set);
    }
}
