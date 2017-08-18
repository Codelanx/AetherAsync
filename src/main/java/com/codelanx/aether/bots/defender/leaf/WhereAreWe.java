package com.codelanx.aether.bots.defender.leaf;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.region.Players;

/**
 * NOTES:
 * We should've been in one of the previously queried places.  Stop the bot
 */
public class WhereAreWe implements Runnable {

    private DefenderBot bot;

    public WhereAreWe(DefenderBot bot) {this.bot = bot;}

    @Override
    public void run() {
        Environment.getBot().getLogger().info(Players.getLocal().getPosition());
        Environment.getBot().stop();
    }
}
