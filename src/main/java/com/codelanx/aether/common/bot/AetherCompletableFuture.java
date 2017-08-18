package com.codelanx.aether.common.bot;

import com.codelanx.commons.util.Scheduler;

/**
 * Created by rogue on 8/14/2017.
 */
public class AetherCompletableFuture<E> extends ProxiedCompletableFuture<E> {

    public AetherCompletableFuture() {
        super(Scheduler.getService());
    }
}
