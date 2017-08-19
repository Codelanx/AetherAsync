package com.codelanx.aether.bots.defender.leaf;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.common.input.UserInput;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.script.Execution;

/**
 * NOTES:
 * 
 */
public class GoDown implements Runnable {

    private DefenderBot bot;

    public GoDown(DefenderBot bot) {this.bot = bot;}

    GameObject stairs;
    @Override
    public void run() {
        stairs = GameObjects.newQuery().names("Staircase").results().nearest();
        if(stairs != null){
            if(stairs.isVisible()) {
                UserInput.interact(stairs, "Climb-down").postAttempt().thenRun(() -> {
                    Execution.delay(1000, 2000);
                });
            } else {
                Camera.turnTo(stairs);
            }
        } else {
            Environment.getBot().getLogger().info("Stairs were null");
        }
    }
}
