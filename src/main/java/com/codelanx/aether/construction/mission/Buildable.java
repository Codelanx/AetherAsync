package com.codelanx.aether.construction.mission;

import com.codelanx.aether.common.Identifiable;

public interface Buildable extends Identifiable {

    public Destructable getResult();
}
