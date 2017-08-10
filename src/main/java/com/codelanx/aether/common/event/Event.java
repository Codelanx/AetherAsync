package com.codelanx.aether.common.event;

public abstract class Event {

    private String name;

    public abstract HandlerList getStaticHandlerList();

    public String getEventName() {
        if (name == null) {
            name = getClass().getSimpleName();
        }
        return name;
    }

}
