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
public class GoToShop implements Runnable {

    private DefenderBot bot;

    public GoToShop(DefenderBot bot) {this.bot = bot;}

    private Area shop = new Area.Rectangular(new Coordinate(2838, 3548, 0), new Coordinate(2843, 3555, 0));

    @Override
    public void run() {
        WebPath newPath = Traversal.getDefaultWeb().getPathBuilder().buildTo(shop.getRandomCoordinate());
        if(newPath != null){
            newPath.step();
        } else {
            Environment.getBot().getLogger().info("Web path generated was null");
        }
    }
}
