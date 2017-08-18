package com.codelanx.aether.bots.defender.leaf;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;

/**
 * NOTES:
 * 
 */
public class GoToBasement implements Runnable {

    private DefenderBot bot;

    public GoToBasement(DefenderBot bot) {this.bot = bot;}

    private Area nearLadder = new Area.Circular(new Coordinate(2834, 3542, 0), 3);
    private Area shop = new Area.Rectangular(new Coordinate(2838, 3548, 0), new Coordinate(2843, 3555, 0));
    private Area doorArea = new Area.Rectangular(new Coordinate(2837, 3549, 0), new Coordinate(2838, 3549, 0));
    private GameObject closedDoor;
    private GameObject openDoor;

    @Override
    public void run() {
        if(nearLadder.contains(Players.getLocal())){
            GameObject ladder = GameObjects.newQuery().names("Ladder").within(nearLadder).results().first();
            if(ladder != null){
                if(ladder.isVisible()) {
                    ladder.interact("Climb-down");
                } else {
                    Camera.turnTo(ladder);
                }
            }
        } else if (Players.getLocal() != null && Players.getLocal().getPosition().getX() > 2838 && !shop.contains(Players.getLocal())) {
            WebPath newPath = Traversal.getDefaultWeb().getPathBuilder().buildTo(shop.getRandomCoordinate());
            if (newPath != null) {
                newPath.step();
            } else {
                Environment.getBot().getLogger().info("Web path generated was null");
            }
        } else if (Players.getLocal() != null && Players.getLocal().getPosition().getX() > 2838 && shop.contains(Players.getLocal())){
            if((closedDoor = GameObjects.newQuery().names("Door").actions("Open").within(doorArea).results().first()) != null){
                if(closedDoor.isVisible()) {
                    if (closedDoor.interact("Open")) {
                        Execution.delay(500, 3000);
                    }
                } else {
                    Camera.turnTo(closedDoor);
                }
            }
        } else {
            BresenhamPath toLadder = BresenhamPath.buildTo(nearLadder.getRandomCoordinate());
            if(toLadder != null){
                toLadder.step();
            } else {
                Environment.getBot().getLogger().info("toLadder path was null");
            }
        }
    }
}
