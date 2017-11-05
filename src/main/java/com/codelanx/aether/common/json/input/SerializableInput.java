package com.codelanx.aether.common.json.input;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SerializableInput {

    private final InputType type;
    private final List<Integer> componentTarget = new ArrayList<>();
    /*private final Pattern makeAllTargetPattern;
    private final String makeAllTargetString;*/

    public SerializableInput(Object value) {
        this.type = null; //TODO: fuck me just compile
    }

    public SerializableInput(InputType type) {
        this.type = type;
    }

    public InputType getType() {
        return this.type;
    }

    public void queue() {
        switch (this.type) {
            case OLD:
                //TODO: Old handling
                break;
            case COMPONENT:
                //TODO: component handling
                break;
            case MAKEALL:
                //TODO: Makeall handling
                break;
            case NONE:
                return;
        }
    }

    public void act() {
        switch (this.type) {
            case OLD:
                //TODO: Old handling
                break;
            case COMPONENT:
                //TODO: component handling
                break;
            case MAKEALL:
                //TODO: Makeall handling
                break;
            case NONE:
                return;
        }
    }
}
