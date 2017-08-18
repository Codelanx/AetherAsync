package com.codelanx.aether.bots.defender.leaf;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.runemate.game.api.hybrid.Environment;

/**
 * NOTES:
 * 
 */
public class Stop implements Runnable {

    private DefenderBot bot;

    public Stop(DefenderBot bot) {this.bot = bot;}

    @Override
    public void run() {
        Environment.getBot().getLogger().info("Stopping bot.");
        Environment.getBot().stop();
    }
}
