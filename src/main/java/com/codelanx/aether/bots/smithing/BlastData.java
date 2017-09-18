package com.codelanx.aether.bots.smithing;

import com.codelanx.commons.config.ConfigFile;
import com.codelanx.commons.config.MemoryConfig;

import java.util.concurrent.atomic.AtomicReference;

//this is a bit dangerous, there's no type safety if we end up needing more than numbers
public enum BlastData implements MemoryConfig<Object> {

    COAL_IN_FURNACE(0),
    ORES_IN_FURNACE(0),
    HAS_ICE_GLOVES(false),
    HAS_GOLDSMITH_GAUNTLETS(false),
    ;

    public static final int ORE_PER_TRIP = 25;
    public static final int ORE_RECLAIM_LIMIT = 50; //at how many ores in furnace do we start picking up bars?
    public static final int COAL_FILL_LIMIT = 250; //at what point do we decide there's enough coal to start ores full-time?

    private final AtomicReference<Object> data = new AtomicReference<>();

    private BlastData(Object data) {
        this.data.set(data);
    }

    @Override
    public void setValue(Object val) {
        this.data.set(val);
    }

    @Override
    public Object getValue() {
        return this.data.get();
    }
}
