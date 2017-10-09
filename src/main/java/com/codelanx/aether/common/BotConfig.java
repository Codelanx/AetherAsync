package com.codelanx.aether.common;

import com.codelanx.commons.config.MemoryConfig;

import java.util.concurrent.atomic.AtomicReference;

public enum BotConfig implements MemoryConfig<Object> {
    USES_BANK_PLACEHOLDER(false),
    ;

    private final AtomicReference<Object> value = new AtomicReference<>();

    private BotConfig(Object value) {
        this.value.set(value);
    }

    @Override
    public Object getValue() {
        return this.value.get();
    }

    @Override
    public void setValue(Object val) {
        this.value.set(val);
    }
}
