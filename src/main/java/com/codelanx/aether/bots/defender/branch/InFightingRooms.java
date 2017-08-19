package com.codelanx.aether.bots.defender.branch;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;

import java.util.function.Supplier;

//import path.to.your.DesiredItemOnGround
//import path.to.your.OnTopFloor

/**
 * NOTES:
 * Checks if we are in the animation room or either of the cyclops insideCave rooms
 */
public class InFightingRooms extends AetherTask<Boolean> {

    private static final Area topFloorOutside = new Area.Rectangular(new Coordinate(2846, 3536, 2), new Coordinate(2838, 3542, 2));
    private static final Area basementOutside = new Area.Rectangular(new Coordinate(2905, 9966, 0), new Coordinate(2911, 9973, 0));
    private static final  Area animationRoom = new Area.Rectangular(new Coordinate(2861, 3545, 0), new Coordinate(2849, 3534, 0));

    public InFightingRooms(DefenderBot bot) {
        this.register(true, new DesiredItemOnGround(bot));
        this.register(false, new OnTopFloor(bot));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> {
            boolean back = Players.getLocal() != null &&
                    (Players.getLocal().getPosition().getPlane() == 2 && !topFloorOutside.contains(Players.getLocal())) || //on the top floor but not outside the insideCave area
                    (Players.getLocal().getPosition().getPlane() == 0 && Distance.between(new Coordinate(2907, 9968, 0), Players.getLocal().getPosition()) < 200 && !basementOutside.contains(Players.getLocal()) || //in the basement but not outside the insideCave area
                            (animationRoom.contains(Players.getLocal()))); //in the insideCave area of the main floor
            if (back) {
                Aether.getBot().getLogger().info("We found that the local player was in a insideCave room from the 'InFightingRoom' branch");
            }
            return back;
        };
    }
}
