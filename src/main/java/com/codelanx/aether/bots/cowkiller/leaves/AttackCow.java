package com.codelanx.aether.bots.cowkiller.leaves;

import com.codelanx.aether.bots.cowkiller.CowKiller;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.entities.status.CombatGauge;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;

public class AttackCow implements Runnable {

    @Override
    public void run() {
        Npc cow = Npcs.getLoadedWithin(CowKiller.COW_AREA, "Cow").sortByDistance().limit(0, 3).random();
        Player player = Players.getLocal();
        if (cow != null && player != null) {
            if (!cow.isVisible()) {
                Camera.turnTo(cow);
            }
            if (!player.isMoving()) {
                cow.interact("Attack", "Cow");
                Execution.delay(1000, 1500);
                Execution.delayUntil(() -> {
                    CombatGauge gauge = cow.getHealthGauge();
                    return gauge == null || gauge.getPercent() <= 0 || !cow.isValid();
                }, 8000);
            }
        }
    }

}