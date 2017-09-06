package com.codelanx.aether.bots.cowkiller.leaves;

import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.region.Players;

public class Walk implements Runnable {

    private final Area area;

    public Walk(Area area) {
        this.area = area;
    }

    public void run() {
        Player player = Players.getLocal();
        if (player != null) {
            final WebPath path = Traversal.getDefaultWeb().getPathBuilder().buildTo(this.area.getRandomCoordinate());
            if (path != null) {
                path.step();
            }
        }
    }
}
