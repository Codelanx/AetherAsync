package com.codelanx.aether.bots.defender.branch;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.bots.defender.leaf.LeaveRoom;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;

import java.util.function.Supplier;

//import path.to.your.LeaveRoom
//import path.to.your.SufficientHealth

/**
 * NOTES:
 * If we are in a cyclops room, we check for the defender flag, being out of food, or being low on tokens.  If we are in the animation room, we check for having sufficient tokens or being out of food.
 */
public class NeedToLeaveRoom extends AetherTask<Boolean> {

    private static final Area topFloorOutside = new Area.Rectangular(new Coordinate(2846, 3536, 2), new Coordinate(2838, 3542, 2));
    private static final Area basementOutside = new Area.Rectangular(new Coordinate(2905, 9966, 0), new Coordinate(2911, 9973, 0));
    private static final Area animationRoom = new Area.Rectangular(new Coordinate(2861, 3545, 0), new Coordinate(2849, 3534, 0));
    private DefenderBot bot;

    public NeedToLeaveRoom(DefenderBot bot) {
        this.bot = bot;
        this.register(true, new LeaveRoom(bot));
        this.register(false, new SufficientHealth(bot));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> {
            boolean inTopFloorFightRoom = Players.getLocal().getPosition().getPlane() == 2 && !topFloorOutside.contains(Players.getLocal());
            boolean inBasementFightRoom = Players.getLocal().getPosition().getPlane() == 0 && Distance.between(new Coordinate(2907, 9968, 0), Players.getLocal().getPosition()) < 200 && !basementOutside.contains(Players.getLocal());
            boolean inAnimRoomWithTokens = animationRoom.contains(Players.getLocal()) && Inventory.getQuantity("Warrior guild token") >= 2000;

            return Inventory.newQuery().actions("Eat").results().size() == 0 ||
                    (Players.getLocal() != null && (((inBasementFightRoom || inTopFloorFightRoom) && bot.getNewDefender()) || inAnimRoomWithTokens));
        };
    }
}
