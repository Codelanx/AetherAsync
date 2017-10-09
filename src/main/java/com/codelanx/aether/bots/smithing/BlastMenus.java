package com.codelanx.aether.bots.smithing;

import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.cache.GameCache;
import com.codelanx.aether.common.cache.Queryable;
import com.codelanx.aether.common.cache.query.ComponentInquiry;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceComponent;

import java.util.concurrent.atomic.AtomicReference;

public enum BlastMenus implements Queryable<InterfaceComponent, ComponentInquiry> {

    BAR_DISPENSER {
        @Override
        public ComponentInquiry toUncachedInquiry() {
            return ComponentInquiry.builder()
                    .name("")
                    .container(28)
                    .text("Blast Furnace Bar Stock")
                    .build();
            //105: 'gold %1'
            //115: '(All)' (click this for getting all gold
            //107: 'coal to add'
        }
    },
    ;

    private final AtomicReference<ComponentInquiry> inq = new AtomicReference<>();

    @Override
    public AtomicReference<ComponentInquiry> getReferenceToInquiry() {
        return this.inq;
    }

    @Override
    public GameCache<InterfaceComponent, ComponentInquiry> getGlobalCache() {
        return Caches.forInterfaces();
    }
}
