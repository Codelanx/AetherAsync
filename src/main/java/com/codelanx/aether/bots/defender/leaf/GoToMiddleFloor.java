package com.codelanx.aether.bots.defender.leaf;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;

/**
 * NOTES:
 * 
 */
public class GoToMiddleFloor implements Runnable {

    private DefenderBot bot;

    public GoToMiddleFloor(DefenderBot bot) {this.bot = bot;}

    private Area nearStairs = new Area.Circular(new Coordinate(2841, 3538, 0), 4);
    private Coordinate midFloor = new Coordinate(2840, 3539, 1);
    @Override
    public void run() {
        WebPath newPath = Traversal.getDefaultWeb().getPathBuilder().buildTo(midFloor);
        if(newPath != null){
            newPath.step();
        } else {
            Environment.getBot().getLogger().info("Web path generated was null");
        }
    }
}
