package com.codelanx.aether.bots.defender.leaf;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.script.Execution;

/**
 * NOTES:
 * 
 */
public class Wait implements Runnable {

    private DefenderBot bot;

    public Wait(DefenderBot bot) {this.bot = bot;}

    @Override
    public void run() {
        Environment.getBot().getLogger().info("Waiting");
        Execution.delay(500);
    }
}
