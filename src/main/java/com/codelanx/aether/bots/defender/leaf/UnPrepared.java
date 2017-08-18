package com.codelanx.aether.bots.defender.leaf;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.runemate.game.api.hybrid.Environment;

/**
 * NOTES:
 * Since we dont have coins in inv, rol equipped, and mith armor pieces in inv, we want to stop here.
 */
public class UnPrepared implements Runnable {

    private DefenderBot bot;

    public UnPrepared(DefenderBot bot) {this.bot = bot;}

    @Override
    public void run() {
        Environment.getBot().getLogger().info("User did not have required items");
        Environment.getBot().stop();
    }
}
