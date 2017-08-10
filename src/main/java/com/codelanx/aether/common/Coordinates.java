package com.codelanx.aether.common;

import com.runemate.game.api.hybrid.location.Coordinate;

import java.util.HashMap;
import java.util.Map;

public class Coordinates {

    private static final Map<Integer, Coordinate> hashedCoords = new HashMap<>();

    public static Coordinate of(int x, int y, int z) {
        return Coordinates.hashedCoords.computeIfAbsent(
                Coordinates.anonHashcode(x, y, z),
                k -> new Coordinate(x, y, z)
        );
    }

    private static int anonHashcode(int x, int y, int z) {
        return z << 28 | y << 14 | x;
    }

}
