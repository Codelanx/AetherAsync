package com.codelanx.aether.bots.defender.leaf;

import com.codelanx.aether.bots.defender.DefenderBot;

/**
 * NOTES:
 * For now we just wait, ideally we would close the shop and hop to a different world
 */
public class WaitForNow implements Runnable {

    private DefenderBot bot;

    public WaitForNow(DefenderBot bot) {this.bot = bot;}

    @Override
    public void run() {

    }
}
