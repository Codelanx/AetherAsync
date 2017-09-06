package com.codelanx.aether.bots.cowkiller.leaves;

import com.codelanx.aether.bots.cowkiller.CowKiller;
import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.region.GroundItems;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.rs3.local.hud.interfaces.LootInventory;
import com.runemate.game.api.script.Execution;

public class LootHide implements Runnable {

    @Override
    public void run() {
        Player player = Players.getLocal();
        if (player != null) {
            if (!player.isMoving()) {
                GroundItem hide = GroundItems.newQuery().names("Cowhide").within(CowKiller.COW_AREA).results().nearest();

                if (hide != null) {
                    if (!hide.isVisible()) {
                        Camera.turnTo(hide);
                        Execution.delay(2000, 3000);
                    } else {
                        if (!LootInventory.isEnabled()) {
                            if (hide.interact("Take")) {
                                Execution.delayUntil(() -> !hide.isValid(), 3000);
                            } else {
                                Camera.concurrentlyTurnTo(hide);
                            }
                        } else {
                            if (hide.interact("Take")) {
                                Execution.delayUntil(LootInventory::isOpen, 3000);
                                while (LootInventory.contains("Cowhide") && LootInventory.isOpen()) {
                                    LootInventory.take("Cowhide");
                                    Execution.delay(1500, 2000);
                                }
                                LootInventory.close();
                            } else {
                                Camera.concurrentlyTurnTo(hide);
                            }
                        }
                    }
                }
            }
        }
    }
}