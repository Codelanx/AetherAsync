package com.codelanx.aether.common.bot.async.mouse;

import com.codelanx.aether.common.bot.async.AetherAsyncBot;
import com.runemate.game.api.hybrid.entities.details.Interactable;

import java.util.HashMap;
import java.util.Map;

public enum ClickHandler {

    ;

    private final Map<?, ?> map = new HashMap<>();
    private final AetherAsyncBot bot;

    private ClickHandler(AetherAsyncBot bot) {
        this.bot = bot;
    }

    public void registerClick(Interactable obj) {
        obj.click();
    }

    public void attempt() {
        MouseTarget target = this.getNextTarget();
        if (target == null) {
            return;
        }
        switch (target.getType()) {

        }
    }

    public static void interact(Interactable obj, String value) {

    }

    public static void click(Interactable obj) {

    }

    public static boolean hasTasks() {
        return false;
    }

    private MouseTarget getNextTarget() {
        return null;
    }
}
