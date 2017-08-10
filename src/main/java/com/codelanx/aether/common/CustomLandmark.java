package com.codelanx.aether.common;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.details.Locatable;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.web.vertex_types.objects.BasicObjectVertex;
import com.runemate.game.api.hybrid.location.navigation.web.vertex_types.objects.UseItemOnObjectVertex;
import com.runemate.game.api.hybrid.region.Players;

import java.util.concurrent.ThreadLocalRandom;

public enum CustomLandmark {

    RANGE(true),
    FURNACE(false),
    ;

    private Coordinate[] coords;

    private CustomLandmark(boolean debug) {
        if (debug) {
            Environment.getLogger().info("Verticies:");
            Environment.getLogger().info("\tUseItemOnObjectVertex:");
            Traversal.getDefaultWeb().getVertices(UseItemOnObjectVertex.class).forEach(System.out::println);
            Environment.getLogger().info("\tBasicObjectVertex:");
            Traversal.getDefaultWeb().getVertices(BasicObjectVertex.class).forEach(System.out::println);
        }
    }



}
