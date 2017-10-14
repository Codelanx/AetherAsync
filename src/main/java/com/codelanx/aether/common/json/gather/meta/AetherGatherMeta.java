package com.codelanx.aether.common.json.gather.meta;

import com.codelanx.commons.util.OptimisticLock;

public class AetherGatherMeta implements GatherMeta {

    private final String key;
    private final String label;
    private final OptimisticLock lock = new OptimisticLock();
    private Object value;

    public AetherGatherMeta(String key, Object value, String label) {
        this.key = key;
        this.value = value;
        this.label = label;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public Object getValue() {
        return this.lock.read(() -> this.value);
    }

    @Override
    public void setValue(Object value) {
        this.lock.write(() -> this.value = value);
    }

    @Override
    public String getLabel() {
        return this.label;
    }
}
