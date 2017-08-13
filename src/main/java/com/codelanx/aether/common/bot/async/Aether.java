package com.codelanx.aether.common.bot.async;

public class Aether {

    private static AetherAsyncBot instance;

    public static AetherScheduler getScheduler() {
        return Aether.getBot().getScheduler();
    }

    public static AetherAsyncBot getBot() {
        return instance;
    }

    static void setBot(AetherAsyncBot instance) {
        Aether.instance = instance;
    }
}
