package com.codelanx.aether.bots.defender.branch;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;

import java.util.function.Supplier;

/**
 * NOTES:
 * 
 */
public class OnBasement extends AetherTask<Boolean> {

    public OnBasement(DefenderBot bot) {
        this.register(true, new WithDragonDefender(bot));
        this.register(false, new OnMainFloor(bot));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> Players.getLocal() != null && Players.getLocal().getPosition().getPlane() == 0 && Distance.between(new Coordinate(2907, 9968, 0), Players.getLocal().getPosition()) < 200;
    }
}
