package com.codelanx.aether.bots.cowkiller.leaves;

import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;

public class TanHides implements Runnable {

    @Override
    public void run() {
        Npc jack = Npcs.newQuery().names("Jack Oval").results().nearest();
        Player player = Players.getLocal();
        if (jack != null && player != null && !player.isMoving()) {
            Camera.concurrentlyTurnTo(jack);
            //Execution.delay(2000, 3000);
            jack.interact("Tan hide", "Jack Oval");
            Execution.delayUntil(() -> !Inventory.contains("Cowhide"), 3000);
        }
    }

}