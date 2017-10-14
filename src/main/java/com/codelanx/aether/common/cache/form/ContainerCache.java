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
import com.codelanx.commons.util.OptimisticLock;
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
    private final OptimisticLock lock = new OptimisticLock();
    private final OptimisticLock backingLock = new OptimisticLock();
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

    public int update(MaterialInquiry inq, int amount) {
        if (true) { //TODO: We may be removing this method, especially if the listener pays off
            return 0;
        }
        if (amount == 0) {
            return this.lock.read(() -> this.offset.get(inq));
        }
        int back = this.lock.write(() -> {
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

    //returns whole container, reduced to 1 ItemStack with a quantity of the amount in the container
    public Map<Material, Integer> getReducedItems() {
        if (this.type != QueryType.INVENTORY) {
            return Collections.emptyMap(); //design is fucked enough atm that this won't work
        }
        RestLoader loader = Aether.getBot().getData();
        return Arrays.stream(this.backing)
                .collect(Collectors.toMap(loader::fromSpriteItem, SpriteItem::getQuantity, Integer::sum, LinkedHashMap::new));
    }

    private void change(SpriteItem item, int amount) {
        //this.get(new SerializableMaterial(item.getDefinition()).toInquiry()).
    }

    public Stream<SpriteItem> getAll() {
        return Arrays.stream(this.backing).filter(Objects::nonNull);
    }




    //inventory



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


    //item

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
    public QueryType getType() {
        return this.type;
    }

    @Override
    public long getLifetimeMS() {
        //return this.type == QueryType.INVENTORY ? Integer.MAX_VALUE : TimeUnit.SECONDS.toMillis(2);
        return TimeUnit.SECONDS.toMillis(1);
    }

    @Override
    public Supplier<SpriteItemQueryBuilder> getRawQuery() {
        return this.target;
    }

    @Override
    public Supplier<SpriteItemQueryResults> getRunemateResults(MaterialInquiry inquiry) {
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
}
