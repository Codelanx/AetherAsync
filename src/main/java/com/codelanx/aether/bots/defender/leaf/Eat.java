package com.codelanx.aether.bots.defender.leaf;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;

/**
 * NOTES:
 * 
 */
public class Eat implements Runnable {

    private DefenderBot bot;

    public Eat(DefenderBot bot) {this.bot = bot;}

    SpriteItem food;

    @Override
    public void run() {
        Environment.getBot().getLogger().info("In eat leaf");
        if(Players.getLocal() != null) {
            food = Inventory.newQuery().unnoted().actions("Eat").results().first();
            if(food != null) {
                food.interact("Eat");
                Execution.delay(250, 450);
            }
        }
    }
}
