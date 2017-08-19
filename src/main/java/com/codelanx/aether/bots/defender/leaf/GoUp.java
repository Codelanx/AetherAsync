package com.codelanx.aether.bots.defender.leaf;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.common.input.UserInput;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.script.Execution;

/**
 * NOTES:
 * 
 */
public class GoUp implements Runnable {

    private DefenderBot bot;

    public GoUp(DefenderBot bot) {this.bot = bot;}

    @Override
    public void run() {
        GameObject stairs = GameObjects.newQuery().names("Staircase").results().nearest();
        if(stairs != null){
            if(stairs.isVisible()) {
                UserInput.interact(stairs, "Climb-up").postAttempt().thenRun(() -> {
                    Execution.delay(1000, 2000);
                });
            } else {
                Camera.turnTo(stairs);
            }
        }
    }
}
