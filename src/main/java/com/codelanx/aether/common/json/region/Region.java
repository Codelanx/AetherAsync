package com.codelanx.aether.common.json.region;

import com.runemate.game.api.hybrid.location.Coordinate;

public interface Region {

    public Coordinate getMinimumPoint();

    public Coordinate getMaximumPoint();

    public String getName();
}
