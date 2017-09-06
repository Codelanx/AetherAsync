package com.codelanx.aether.common.bot;

public class Aether {

    private static AsyncBot instance;

    public static AetherScheduler getScheduler() {
        return Aether.getBot().getScheduler();
    }

    public static AsyncBot getBot() {
        return instance;
    }

    static void setBot(AsyncBot instance) {
        Aether.instance = instance;
    }
}
