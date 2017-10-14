package com.codelanx.aether.common.cache.form.container;

import com.codelanx.aether.common.cache.QueryType;
import com.codelanx.aether.common.cache.query.MaterialInquiry;
import com.codelanx.aether.common.json.item.Material;
import com.codelanx.aether.common.json.item.Materials;
import com.codelanx.commons.util.OptimisticLock;
import com.codelanx.commons.util.Parallel;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.queries.SpriteItemQueryBuilder;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FixedSizeItemCache extends ItemCache {

    //SpriteItem(int id, int quantity[, int index, Origin cameFrom])
    protected static final SpriteItem EMPTY_SENTINEL = new SpriteItem(-1, -1);
    private final int size;
    private final SpriteItem[] backing;
    private final StampedLock[] locks;
    private final OptimisticLock totalLock = new OptimisticLock();

    public FixedSizeItemCache(int size) {
        this.size = size;
        this.backing = new SpriteItem[this.getCapacity()];
        this.locks = new StampedLock[this.getCapacity()];
        IntStream.range(0, this.locks.length).forEach(i -> this.locks[i] = new StampedLock());
    }

    @Override
    protected void onInvalidate(MaterialInquiry inq, SpriteItem item) {
        super.onInvalidate(inq, item);
        if (item != null) {
            for (int i = 0; i < this.backing.length; i++) {
                SpriteItem curr = this.get(i);
                if (curr == null) {
                    continue;
                }
                if (curr.getIndex() == item.getIndex()) {
                    if (Objects.equals(Materials.getMaterial(curr), Materials.getMaterial(item))) {
                        this.set(i, null);
                    }
                }
            }
            return;
        }
        if (inq == null) {
            Parallel.StampLocks.write(this.totalLock, () -> Arrays.fill(this.backing, null));
        } else {
            Material raw = inq.getMaterial();
            for (int i = 0; i < this.backing.length; i++) {
                int fi = i;
                StampedLock lock = this.locks[i];
                Parallel.StampLocks.optimisticRead(lock, stamp -> {
                    SpriteItem curr = this.backing[fi];
                    if (!raw.equals(Materials.getMaterial(curr))) {
                        return null;
                    }
                    stamp = lock.tryConvertToReadLock(stamp);
                    if (stamp == 0 || !lock.validate(stamp)) {
                        stamp = lock.readLock();
                    }
                    try {
                        SpriteItem now = this.backing[fi];
                        if (now != curr) {
                            //concurrent modification
                            return null;
                        }

                    } finally {
                        lock.unlock(stamp);
                    }
                    if (!lock.validate(stamp)) {
                        stamp = lock.readLock();
                        try {
                            curr = this.backing[fi];
                        } finally {
                            lock.unlockRead(stamp);
                        }
                    } else {
                        stamp = lock.tryConvertToWriteLock(stamp);

                    }
                    return null;
                });
                SpriteItem curr = this.get(i);
                if (curr == null) {
                    continue;
                }
                if (raw.equals(Materials.getMaterial(curr))) {
                    this.set(i, null);
                }
            }
        }
    }

    public int getCapacity() {
        return this.size;
    }

    protected SpriteItem get(int index) {
        return Parallel.StampLocks.optimisticRead(this.locks[index], () -> this.backing[index]);
    }

    protected SpriteItem set(int index, SpriteItem value) {
        return Parallel.StampLocks.write(this.locks[index], () -> this.backing[index] = value);
    }

    @Override
    public QueryType getType() {
        return QueryType.INVENTORY;
    }

    @Override
    public Supplier<? extends SpriteItemQueryBuilder> getRawQuery() {
        return Inventory::newQuery;
    }

    @Override
    public long getLifetimeMS() {
        return -1;
    }

    @Override
    protected <R> R getRawStream(Function<Stream<SpriteItem>, R> andThen) {
        return Parallel.StampLocks.optimisticRead(this.totalLock, () -> andThen.apply(Arrays.stream(this.backing)));
    }

    @Override
    public Stream<SpriteItem> getAllLoaded() {
        return Arrays.stream(this.getLoadedArray()).filter(Objects::nonNull);
    }

    //makes a copy
    public SpriteItem[] getLoadedArray() {
        SpriteItem[] back = new SpriteItem[this.getCapacity()];
        this.totalLock.read(() -> System.arraycopy(this.backing, 0, back, 0, back.length));
        return back;
    }

    @Override
    protected void load(SpriteItem item) {
        this.set(item.getIndex(), item);
    }
}
