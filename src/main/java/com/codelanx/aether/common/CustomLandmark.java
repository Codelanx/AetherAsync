package com.codelanx.aether.common;

import com.codelanx.commons.logging.Logging;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.web.vertex_types.objects.BasicObjectVertex;
import com.runemate.game.api.hybrid.location.navigation.web.vertex_types.objects.UseItemOnObjectVertex;


public enum CustomLandmark {

    RANGE(true),
    FURNACE(false),
    ;

    private Coordinate[] coords;

    private CustomLandmark(boolean debug) {
        if (debug) {
            Logging.info("Verticies:");
            Logging.info("\tUseItemOnObjectVertex:");
            Traversal.getDefaultWeb().getVertices(UseItemOnObjectVertex.class).forEach(System.out::println);
            Logging.info("\tBasicObjectVertex:");
            Traversal.getDefaultWeb().getVertices(BasicObjectVertex.class).forEach(System.out::println);
        }
    }



}
