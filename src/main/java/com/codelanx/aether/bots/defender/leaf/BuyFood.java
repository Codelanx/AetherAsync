package com.codelanx.aether.bots.defender.leaf;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.Shop;

/**
 * NOTES:
 * 
 */
public class BuyFood implements Runnable {

    private DefenderBot bot;

    public BuyFood(DefenderBot bot) {this.bot = bot;}

    @Override
    public void run() {
        Environment.getBot().getLogger().info("In BuyFood leaf");
        if(Inventory.getEmptySlots() > 3){
            if(Shop.getQuantity("Potato with cheese") >= 1){
                Shop.buy("Potato with cheese", 0);
            } else if (Shop.getQuantity("Bass") >= 1){
                Shop.buy("Bass", 0);
            } else if (Shop.getQuantity("Plain pizza") >= 1){
                Shop.buy("Plain pizza", 0);
            }
        } else {
            Environment.getBot().getLogger().info("We are done shopping");
            Shop.close();
        }
    }
}
