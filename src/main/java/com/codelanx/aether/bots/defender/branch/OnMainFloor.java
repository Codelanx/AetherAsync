package com.codelanx.aether.bots.defender.branch;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.bots.defender.leaf.WhereAreWe;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;

import java.util.function.Supplier;

//import path.to.your.WithCoinsRolAndMithArmor
//import path.to.your.WhereAreWe

/**
 * NOTES:
 * 
 */
public class OnMainFloor extends AetherTask<Boolean> {

    public OnMainFloor(DefenderBot bot) {
        this.register(true, new WithCoinsRolAndMithArmor(bot));
        this.register(false, new WhereAreWe(bot));
    }

    private WithCoinsRolAndMithArmor withcoinsrolandmitharmor;
    private WhereAreWe wherearewe;

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> Players.getLocal() != null && Players.getLocal().getPosition().getPlane() == 0 && Distance.between(new Coordinate(2867, 3544, 0), Players.getLocal().getPosition()) < 200;
    }

}
