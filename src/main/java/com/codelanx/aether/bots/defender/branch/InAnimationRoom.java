package com.codelanx.aether.bots.defender.branch;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.bots.defender.leaf.AnimateArmor;
import com.codelanx.aether.bots.defender.leaf.AttackNearestAvailCyclops;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.Players;

import java.util.function.Supplier;

/**
 * NOTES:
 * 
 */
public class InAnimationRoom extends AetherTask<Boolean> {

    private static final Area animationRoom = new Area.Rectangular(new Coordinate(2861, 3545, 0), new Coordinate(2849, 3534, 0));

    public InAnimationRoom(DefenderBot bot) {
        this.register(true, new AnimateArmor(bot));
        this.register(false, new AttackNearestAvailCyclops(bot));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> animationRoom.contains(Players.getLocal());
    }

}
