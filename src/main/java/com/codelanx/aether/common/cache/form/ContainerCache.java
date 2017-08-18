package com.codelanx.aether.common.cache.form;

import com.codelanx.aether.common.cache.GameCache;
import com.codelanx.aether.common.cache.query.MaterialInquiry;
import com.codelanx.commons.util.Reflections;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * Created by rogue on 8/14/2017.
 */
public class ContainerCache extends GameCache<SpriteItem, MaterialInquiry> {
    
    private final Supplier<SpriteItemQueryBuilder> target;
    private final Map<MaterialInquiry, Integer> offset = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public ContainerCache(Supplier<SpriteItemQueryBuilder> target) {
        this.target = target;
    }
    
    @Override
    public Supplier<SpriteItemQueryResults> getResults(MaterialInquiry inquiry) {
        SpriteItemQueryBuilder query = this.getRawQuery().get();
        query.ids(inquiry.getMaterial().getId());
        query.names(inquiry.getMaterial().getName());
        query.equipable(inquiry.getMaterial().isEquippable());
        query.stacks(inquiry.getMaterial().isStackable());
        return query::results;
    }

    @Override
    public int size(MaterialInquiry inq) {
        //Environment.getLogger().info("ContainerCache#size");
        int back = this.get(inq).peek(i -> Environment.getLogger().info("\t" + i))
                .map(SpriteItem::getQuantity).reduce(0, Integer::sum);
        int offset = Reflections.operateLock(this.lock.readLock(), () -> this.offset.getOrDefault(inq, 0));
        Environment.getLogger().info("[ContainerCache] offset: " + offset + ", back: " + back + ", actual back: " + (back + offset));
        back += offset;
        //Environment.getLogger().info("\tsize (" + inq + "): " + back);
        //Environment.getLogger().info("seasoned: " + this.getResults(inq).get().asList().size());
        //Environment.getLogger().info("raw: " + Inventory.newQuery().ids(inq.getMaterial().getId()).names(inq.getMaterial().getName()).equipable(inq.getMaterial().isEquippable()).stacks(inq.getMaterial().isStackable()).results().asList());
        return back;
    }

    public int update(MaterialInquiry inq, int amount) {
        if (amount == 0) {
            return Reflections.operateLock(this.lock.readLock(), () -> this.offset.get(inq));
        }
        int back = Reflections.operateLock(this.lock.writeLock(), () -> {
            return this.offset.compute(inq, (key, old) -> {
                if (old == null) {
                    old = 0;
                }
                old += amount;
                return old;
            });
        });
        Environment.getLogger().info("[ContainerCache] Updated cache by " + amount + ", new amount: " + back);
        return back;
    }

    @Override
    protected void onInvalidate(MaterialInquiry inq, SpriteItem item) {
        if (item != null) {
            return;
        }
        Runnable inv;
        if (inq == null) {
            inv = this.offset::clear;
        } else {
            inv = () -> this.offset.remove(inq);
        }
        Reflections.operateLock(this.lock.writeLock(), inv);
    }

    @Override
    public Supplier<SpriteItemQueryBuilder> getRawQuery() {
        return this.target;
    }
}
