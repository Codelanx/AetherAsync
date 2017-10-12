package com.codelanx.aether.common.cache.form.container;

import com.codelanx.aether.common.cache.GameCache;
import com.codelanx.aether.common.cache.Queryable;
import com.codelanx.aether.common.cache.query.MaterialInquiry;
import com.codelanx.aether.common.json.item.Material;
import com.codelanx.aether.common.json.item.Materials;
import com.codelanx.commons.util.Lambdas;
import com.codelanx.commons.util.Parallel;
import com.codelanx.commons.util.Scheduler;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ItemCache extends GameCache<SpriteItem, MaterialInquiry> {

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

    @Override
    public abstract Supplier<? extends SpriteItemQueryBuilder> getRawQuery();

    @Override
    public long getLifetimeMS() {
        return 0;
    }

    public int count(MaterialInquiry inq) {
        return ItemCache.count(this.get(inq));
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
    protected CompletableFuture<List<SpriteItem>> schedule(MaterialInquiry inq) {
        return Scheduler.complete(() -> {
            Material m = inq.getMaterial();
            List<SpriteItem> items = this.getRawStream(str -> str.collect(Collectors.toList()));
            items.removeIf(i -> !m.equals(Materials.getMaterial(i)));
            return items;
        });
    }

    public abstract Stream<SpriteItem> getAllLoaded();

    protected abstract <R> R getRawStream(Function<Stream<SpriteItem>, R> andThen);

    //#size will be the stack count, or list size basically

    public void loadAll() {
        this.getRawQuery().get().results().asList().forEach(this::load);
    }

    protected abstract void load(SpriteItem item);
}
