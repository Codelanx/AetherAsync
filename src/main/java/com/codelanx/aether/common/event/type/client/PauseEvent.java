package com.codelanx.aether.common.event.type.client;

import com.codelanx.aether.common.event.Event;
import com.codelanx.aether.common.event.HandlerList;

public class PauseEvent extends Event {

    private static final HandlerList handler = new HandlerList();

    @Override
    public HandlerList getStaticHandlerList() {
        return PauseEvent.handler;
    }
}
