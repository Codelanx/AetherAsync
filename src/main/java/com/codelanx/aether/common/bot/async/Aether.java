package com.codelanx.aether.common.bot.async;

public class Aether {

    public static AetherScheduler getScheduler() {
        return Aether.getBot().getScheduler();
    }

    public static AetherAsyncBot getBot() {
        return AetherAsyncBot.get();
    }
}
