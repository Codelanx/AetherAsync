package com.codelanx.aether.common.cache.form.container;

import com.codelanx.aether.common.cache.QueryType;
import com.codelanx.aether.common.cache.query.MaterialInquiry;
import com.codelanx.aether.common.json.item.Material;
import com.codelanx.aether.common.json.item.Materials;
import com.codelanx.commons.util.Scheduler;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class BankCache extends ItemCache {

    private final Map<Material, SpriteItem> items = new HashMap<>();

    @Override
    public Supplier<? extends SpriteItemQueryBuilder> getRawQuery() {
        return Bank::newQuery;
    }

    @Override
    public Stream<SpriteItem> getAllLoaded() {
        return this.items.values().stream();
    }

    @Override
    protected <R> R getRawStream(Function<Stream<SpriteItem>, R> andThen) {
        return andThen.apply(this.items.values().stream());
    }

    @Override
    protected void load(SpriteItem item) {
        this.items.put(Materials.getMaterial(item), item);
    }

    @Override
    public QueryType getType() {
        return QueryType.BANK;
    }

    @Override
    protected CompletableFuture<List<SpriteItem>> schedule(MaterialInquiry inq) {
        return Scheduler.complete(() -> {
            SpriteItem back = this.items.get(inq.getMaterial());
            return back == null ? Collections.emptyList() : Collections.singletonList(back);
        });
    }
}
