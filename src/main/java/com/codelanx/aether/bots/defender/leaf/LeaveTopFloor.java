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
public class LeaveTopFloor implements Runnable {

    private DefenderBot bot;

    public LeaveTopFloor(DefenderBot bot) {this.bot = bot;}

    @Override
    public void run() {
        GameObject stairs = GameObjects.newQuery().names("Staircase").results().first();
        if(stairs != null){
            if(stairs.isVisible()){
                if(stairs.interact("Climb-down")){
                    Execution.delay(1000, 3000);
                }
            } else {
                Camera.turnTo(stairs);
            }
        }
    }
}
