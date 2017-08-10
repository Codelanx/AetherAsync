package com.codelanx.aether.common.statebot;

import com.runemate.game.api.hybrid.entities.details.Interactable;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

//hmm, central service, or per bot?
public class ClickHandler {

    private static final long MIN_CLICK_MS = 60; //minimum time between clicks, roughly 17 cps
    private long lastClickMS; //last registered click
    private final Map<Interactable, List<Consumer<? super Interactable>>> actions = new LinkedHashMap<>();

    public <T extends Interactable> void registerClick(T target, Consumer<T> action) {
        this.getActionList(target).add((Consumer<? super Interactable>) (Consumer<?>) action); //how2 break type check
    }

    private <T extends Interactable> List<Consumer<? super Interactable>> getActionList(T target) {
        return this.actions.computeIfAbsent(target, k -> new LinkedList<>());
    }

}
