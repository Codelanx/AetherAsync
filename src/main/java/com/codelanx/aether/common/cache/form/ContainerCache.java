package com.codelanx.aether.common.cache.form;

import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.cache.GameCache;
import com.codelanx.aether.common.cache.QueryType;
import com.codelanx.aether.common.cache.Queryable;
import com.codelanx.aether.common.cache.query.MaterialInquiry;
import com.codelanx.aether.common.json.item.Material;
import com.codelanx.aether.common.json.item.Materials;
import com.codelanx.aether.common.rest.RestLoader;
import com.codelanx.commons.logging.Logging;
import com.codelanx.commons.util.Parallel;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.script.framework.listeners.InventoryListener;
import com.runemate.game.api.script.framework.listeners.events.ItemEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by rogue on 8/14/2017.
 */
//container as in, item containers
public class ContainerCache extends GameCache<SpriteItem, MaterialInquiry> implements InventoryListener {

    private final SpriteItem[] backing;
    private final Supplier<SpriteItemQueryBuilder> target;
    private final Map<MaterialInquiry, Integer> offset = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReadWriteLock backingLock = new ReentrantReadWriteLock();
    private final QueryType type;
    
    public ContainerCache(Supplier<SpriteItemQueryBuilder> target, QueryType type) {
        this.target = target;
        this.type = type;
        if (this.type == QueryType.INVENTORY) {
            Aether.getBot().getEventDispatcher().addListener(this);
            this.backing = new SpriteItem[28];
        } else {
            this.backing = new SpriteItem[0];
        }
    }

    public void loadAll() {
        //load full container
        if (this.type == QueryType.INVENTORY) {

        }
    }

    @Override
    public QueryType getType() {
        return this.type;
    }

    @Override
    public long getLifetimeMS() {
        //return this.type == QueryType.INVENTORY ? Integer.MAX_VALUE : TimeUnit.SECONDS.toMillis(2);
        return TimeUnit.SECONDS.toMillis(1);
    }

    @Override
    public Supplier<SpriteItemQueryResults> getResults(MaterialInquiry inquiry) {
        return () -> {
            SpriteItemQueryBuilder query = this.getRawQuery().get();
            query.ids(inquiry.getMaterial().getId());
            query.names(inquiry.getMaterial().getName());
            if (!Environment.isOSRS()) {
                query.equipable(inquiry.getMaterial().isEquippable());
            }
            query.stacks(inquiry.getMaterial().isStackable());
            return query.results();
        };
    }

    public Stream<SpriteItem> getAll() {
        return Arrays.stream(this.backing).filter(Objects::nonNull);
    }

    public int count(MaterialInquiry inq) {
        return ContainerCache.count(this.get(inq));
    }

    public int count(Queryable<SpriteItem, MaterialInquiry> inq) {
        return this.count(inq.toInquiry());
    }

    public static int count(Stream<SpriteItem> stream) {
        return stream.map(SpriteItem::getQuantity).reduce(0, Integer::sum);
    }

    public static Collector<SpriteItem, ?, Integer> counting() {
        return Collectors.reducing(0, SpriteItem::getQuantity, Integer::sum);
    }

    @Override
    public int size(MaterialInquiry inq) {
        //Logging.info("ContainerCache#size");
        int back = this.getCurrent(inq).peek(i -> Logging.info("\t" + i))
                .map(SpriteItem::getQuantity).reduce(0, Integer::sum);
        int offset = Parallel.operateLock(this.lock.readLock(), () -> this.offset.getOrDefault(inq, 0));
        Logging.info("[ContainerCache] offset: " + offset + ", back: " + back + ", actual back: " + (back + offset));
        back += offset;
        //Logging.info("\tsize (" + inq + "): " + back);
        //Logging.info("seasoned: " + this.getResults(inq).get().asList().size());
        //Logging.info("raw: " + Inventory.newQuery().ids(inq.getMaterial().getId()).names(inq.getMaterial().getName()).equipable(inq.getMaterial().isEquippable()).stacks(inq.getMaterial().isStackable()).results().asList());
        return back;
    }

    public int update(MaterialInquiry inq, int amount) {
        if (true) { //TODO: We may be removing this method, especially if the listener pays off
            return 0;
        }
        if (amount == 0) {
            return Parallel.operateLock(this.lock.readLock(), () -> this.offset.get(inq));
        }
        int back = Parallel.operateLock(this.lock.writeLock(), () -> {
            return this.offset.compute(inq, (key, old) -> {
                if (old == null) {
                    old = 0;
                }
                old += amount;
                return old;
            });
        });
        Logging.info("[ContainerCache] Updated cache by " + amount + ", new amount: " + back);
        return back;
    }

    @Override
    protected void onInvalidate(MaterialInquiry inq, SpriteItem item) {
        if (item != null) {
            Parallel.operateLock(this.backingLock.writeLock(), () -> {
                for (int i = 0; i < this.backing.length; i++) {
                    if (this.backing[i] == null) {
                        continue;
                    }
                    if (this.backing[i].getIndex() == item.getIndex()) {
                        if (this.backing[i].getDefinition().equals(item.getDefinition())) {
                            this.backing[i] = null;
                        }
                    }
                }
            });
            return;
        }
        Runnable inv;
        if (inq == null) {
            Parallel.operateLock(this.backingLock.writeLock(), () -> Arrays.fill(this.backing, null));
            inv = this.offset::clear;
        } else {
            Material raw = inq.getMaterial();
            Parallel.operateLock(this.backingLock.writeLock(), () -> {
                for (int i = 0; i < this.backing.length; i++) {
                    if (raw.equals(Materials.getMaterial(this.backing[i]))) {
                        this.backing[i] = null;
                    }
                }
            });
            inv = () -> this.offset.remove(inq);
        }
        Parallel.operateLock(this.lock.writeLock(), inv);
    }

    //returns whole container, reduced to 1 ItemStack with a quantity of the amount in the container
    public Map<Material, Integer> getReducedItems() {
        if (this.type != QueryType.INVENTORY) {
            return Collections.emptyMap(); //design is fucked enough atm that this won't work
        }
        RestLoader loader = Aether.getBot().getData();
        return Arrays.stream(this.backing)
                .collect(Collectors.toMap(loader::fromSpriteItem, SpriteItem::getQuantity, Integer::sum, LinkedHashMap::new));
    }

    @Override
    public Supplier<SpriteItemQueryBuilder> getRawQuery() {
        return this.target;
    }

    @Override
    public void onItemAdded(ItemEvent event) {
        SpriteItem i = event.getItem();
        Logging.info(String.format("[Cache | %s] Item add event called: {index: %d, change: %d, name: %s}", this.type.name(), i.getIndex(), event.getQuantityChange(), i.getDefinition().getName()));
        //Material m = Aether.getBot(); //TODO:
    }

    @Override
    public void onItemRemoved(ItemEvent event) {
        SpriteItem i = event.getItem();
        Logging.info(String.format("[Cache | %s] Item remove event called: {index: %d, change: %d, name: %s}", this.type.name(), i.getIndex(), event.getQuantityChange(), i.getDefinition().getName()));
    }

    private void change(SpriteItem item, int amount) {
        //this.get(new SerializableMaterial(item.getDefinition()).toInquiry()).
    }
}
