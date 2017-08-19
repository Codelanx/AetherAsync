package com.codelanx.aether.bots.defender.leaf;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.common.input.UserInput;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.basic.BresenhamPath;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.script.Execution;

/**
 * NOTES:
 * 
 */
public class ReEnterBasement implements Runnable {

    private DefenderBot bot;

    public ReEnterBasement(DefenderBot bot) {this.bot = bot;}
    private Area topFloorOutside = new Area.Circular(new Coordinate(2846, 3541, 2), 7);
    private Area basementOutside = new Area.Rectangular(new Coordinate(2905, 9966, 0), new Coordinate(2911, 9973, 0));
    private Area topFloorDoor = new Area.Circular(new Coordinate(2846, 3541, 2), 3);
    private Area basementDoor = new Area.Circular(new Coordinate(2911, 9968, 0), 3);
    private GameObject door;

    @Override
    public void run() {
        boolean outsideTopFloorFightRoom = Players.getLocal().getPosition().getPlane() == 2 && topFloorOutside.contains(Players.getLocal());
        boolean outsideBasementFightRoom = Players.getLocal().getPosition().getPlane() == 0 && Distance.between(new Coordinate(2907, 9968, 0), Players.getLocal().getPosition()) < 200 && basementOutside.contains(Players.getLocal());

        if(outsideTopFloorFightRoom){
            door = GameObjects.newQuery().names("Door").within(topFloorDoor).results().first();
        } else if(outsideBasementFightRoom){
            door = GameObjects.newQuery().names("Door").within(basementDoor).results().first();
        } else {
            door = null;
        }

        if(ChatDialog.getContinue() != null){
            ChatDialog.getContinue().select();
        } else if(door != null && Players.getLocal() != null){
            if(door.isVisible()){
                UserInput.interact(door, "Open").postAttempt().thenRun(() -> {
                    Execution.delay(2000, 3000);
                });
            } else if (Distance.between(door.getPosition(), Players.getLocal().getPosition()) > 8){
                BresenhamPath toDoor = BresenhamPath.buildTo(door);
                if(toDoor != null) {
                    toDoor.step();
                } else {
                    Environment.getBot().getLogger().info("Path to door was null");
                }
            } else {
                Camera.turnTo(door);
            }
        } else {
            Environment.getBot().getLogger().info("Door to enter was null");
        }
    }
}
