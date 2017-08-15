package com.codelanx.aether.common.cache;

import com.codelanx.aether.common.json.item.Material;
import com.codelanx.commons.util.Reflections;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CachedInventory {

    private static final boolean INVALIDATE_ZERO = false; //if true, 0 values are not stored and are re-queried when asked for
    //0 is a valid number, null means unqueried
    private final Map<Material, Integer> items = new HashMap<>();

    //amount: negative or positive
    public int update(Material m, int amount) {
        int back = Optional.ofNullable(this.items.compute(m, (key, old) -> {
            if (amount == 0) {
                return old;
            }
            if (old == null) {
                old = 0;
            }
            if (old + amount < 0) {
                throw new IllegalArgumentException("Not enough items in inventory to remove");
            }
            old += amount;
            return old < 0 ? null : INVALIDATE_ZERO && old == 0 ? null : old;
        })).orElse(0);
        Environment.getLogger().info("CachedInventory#update(" + m + ", " + amount + "): " + back + "; " + Reflections.getCaller());
        return back;
    }

    public void invalidateAll() {
        this.items.clear();
    }

    public void invalidate(Material m) {
        this.items.remove(m);
    }

    public void queryAndUpdate(Material m) {
        Environment.getLogger().info("CachedInventory#queryAndUpdate(" + m + "): " + Reflections.getCaller());
        SpriteItemQueryResults res = Inventory.newQuery().names(m.getName()).results();
        int amt = res.stream().map(SpriteItem::getQuantity).reduce(0, Integer::sum);
        this.set(m, amt);
    }

    public Integer set(Material m, int amount) {
        Integer back;
        if (INVALIDATE_ZERO && amount == 0) {
            back = this.items.remove(m);
        } else {
            back = this.items.put(m, amount);
        }
        Environment.getLogger().info("CachedInventory#set(" + m + ", " + amount + "): " + back + "; " + Reflections.getCaller());
        return back;
    }

    //performs a query if needed
    public int get(Material m) {
        Integer back = this.items.get(m);
        if (back == null) {
            this.queryAndUpdate(m);
        }
        back = this.getLoaded(m);
        Environment.getLogger().info("CachedInventory#get(" + m + "): " + back + ";" + Reflections.getCaller());
        return back;
    }

    public int getLoaded(Material m) {
        return this.items.getOrDefault(m, 0);
    }
}
