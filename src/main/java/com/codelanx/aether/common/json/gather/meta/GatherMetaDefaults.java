package com.codelanx.aether.common.json.gather.meta;

public enum GatherMetaDefaults implements GatherMeta {

    USES_RECIPES("uses-recipes", true, "Use recipes?"),
    ;

    private final AetherGatherMeta meta;

    private GatherMetaDefaults(String key, Object def, String label) {
        this.meta = new AetherGatherMeta(key, def, label);
    }

    @Override
    public String getKey() {
        return this.meta.getKey();
    }

    @Override
    public Object getValue() {
        return this.meta.getValue();
    }

    @Override
    public void setValue(Object value) {
        this.meta.setValue(value);
    }

    @Override
    public String getLabel() {
        return this.meta.getLabel();
    }
}
