package com.codelanx.aether.bots.defender.leaf;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;

/**
 * NOTES:
 * 
 */
public class AttackNearestAvailCyclops implements Runnable {

    private DefenderBot bot;

    public AttackNearestAvailCyclops(DefenderBot bot) {this.bot = bot;}

    private Npc availCyclops;

    @Override
    public void run() {
        Environment.getBot().getLogger().info("In AttackNearestAvailCyclops leaf");
        availCyclops = Npcs.newQuery().names("Cyclops").targeting(null, Players.getLocal()).animations(-1).reachable().results().nearest();
        if(availCyclops != null){
            if(availCyclops.isVisible()) {
                if (availCyclops.interact("Attack")) {
                    Execution.delayUntil(() -> Players.getLocal().getTarget() != null, 500, 2000);
                    Execution.delay(500, 1000);
                }
            } else {
                Camera.turnTo(availCyclops);
            }
        }
    }
}
