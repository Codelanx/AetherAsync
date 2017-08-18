package com.codelanx.aether.bots.defender.leaf;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.script.Execution;

/**
 * NOTES:
 * 
 */
public class GoUpLadder implements Runnable {

    private DefenderBot bot;

    public GoUpLadder(DefenderBot bot) {this.bot = bot;}

    @Override
    public void run() {
        GameObject ladder = GameObjects.newQuery().names("Ladder").results().nearest();
        if(ladder != null){
            if(ladder.isVisible()){
            if(ladder.interact("Climb-up")) {
                Execution.delay(1000, 2000);
            }
        } else {
                Camera.turnTo(ladder);
            }
        }
    }
}
