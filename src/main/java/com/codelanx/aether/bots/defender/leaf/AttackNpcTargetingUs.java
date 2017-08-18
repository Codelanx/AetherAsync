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
public class AttackNpcTargetingUs implements Runnable {

    private DefenderBot bot;

    public AttackNpcTargetingUs(DefenderBot bot) {this.bot = bot;}

    Npc targetingPlayer;

    @Override
    public void run() {
        Environment.getBot().getLogger().info("In AttackNpcTargetingUs leaf");
        targetingPlayer = Npcs.newQuery().targeting(Players.getLocal()).reachable().animations(-1).results().nearest();
        if(targetingPlayer != null){
            if(targetingPlayer.isVisible()){
                if(targetingPlayer.interact("Attack")) {
                    Execution.delay(500, 1000);
                }
            } else {
                Camera.turnTo(targetingPlayer);
            }
        }
    }
}
