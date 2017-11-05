package com.codelanx.aether.common.json.input;

import java.util.Map;

//general input, usually
public enum InputType {
    OLD,
    COMPONENT,
    MAKEALL,
    NONE,
    ;

    private static final InputType[] VALUES = InputType.values();

    public static InputType inferrType(Object mapping) {
        if (mapping == null) {
            return null;
        }
        if (!(mapping instanceof Map)) {
            //check against literal values
        }
        Map<String, Object> map = (Map<String, Object>) mapping;
        Object type = map.get("type");
        if (type == null || !(type instanceof String)) {
            //inferr from available keys
        } else {
            return matchType((String) type);
        }
    }

    public static InputType inferrType(Map<String, Object> mapping) {
        Object o = mapping.get("input");
        if (o == null) {
            //check for old fields
            if (mapping.get("component") != null) {
                return InputType.COMPONENT;
            } else if (mapping.get("container") != null && mapping.get("child") != null) {
                return InputType.OLD;
            }
            return InputType.NONE;
        } else if (!(o instanceof Map)) {
            //inferr from literal type
        } else {
            Map<String, Object> map = (Map<String, Object>) o;
            Object type = map.get("type");
            if (type == null || !(type instanceof String)) {
                //inferr from available keys
            } else {
                return matchType((String) type);
            }
        }
    }

    public static InputType matchType(String type) {
        type = type.toUpperCase().replace(' ', '_');
        for (int i = 0; i < VALUES.length; i++) {
            if (VALUES[i].name().equals(type)) {
                return VALUES[i];
            }
        }
        return null;
    }
}
