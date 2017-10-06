package com.codelanx.aether.common.json.gather.meta;

import com.codelanx.commons.util.Reflections;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AetherGatherMeta implements GatherMeta {

    private final String key;
    private final String label;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
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
        return Reflections.operateLock(this.lock.readLock(), () -> this.value);
    }

    @Override
    public void setValue(Object value) {
        Reflections.operateLock(this.lock.writeLock(), () -> this.value = value);
    }

    @Override
    public String getLabel() {
        return this.label;
    }
}
