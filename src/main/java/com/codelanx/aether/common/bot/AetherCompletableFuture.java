package com.codelanx.aether.common.bot;

/**
 * Created by rogue on 8/14/2017.
 */
public class AetherCompletableFuture<E> extends ProxiedCompletableFuture<E> {

    public AetherCompletableFuture() {
        super(Aether.getScheduler().getThreadPool());
    }
}
