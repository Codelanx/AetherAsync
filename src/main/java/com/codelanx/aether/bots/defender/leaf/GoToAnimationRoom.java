package com.codelanx.aether.bots.defender.leaf;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;

/**
 * NOTES:
 * 
 */
public class GoToAnimationRoom implements Runnable {

    private DefenderBot bot;

    public GoToAnimationRoom(DefenderBot bot) {this.bot = bot;}

    private Area animationRoom = new Area.Rectangular(new Coordinate(2861, 3545, 0), new Coordinate(2849, 3534, 0));

    @Override
    public void run() {
        WebPath newPath = Traversal.getDefaultWeb().getPathBuilder().buildTo(animationRoom.getRandomCoordinate());
        if(newPath != null){
            newPath.step();
        }
    }
}
