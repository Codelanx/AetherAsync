package com.codelanx.aether.bots.defender.leaf;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.region.GroundItems;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;

/**
 * NOTES:
 * Pick up defender and set the defender flag true
 */
public class DefenderOnGround implements Runnable {

    private DefenderBot bot;

    public DefenderOnGround(DefenderBot bot) {this.bot = bot;}

    private static final String defenders[] = {"Bronze defender", "Iron defender", "Steel defender", "Black defender", "Mithril defender", "Adamant defender", "Rune defender", "Dragon defender"};
    private static final String desired[] = {"Mithril full helm", "Warrior guild token", "Mithril platebody", "Mithril platelegs", "Bronze defender", "Iron defender", "Steel defender", "Black defender", "Mithril defender", "Adamant defender", "Rune defender", "Dragon defender"};

    @Override
    public void run() {
        Environment.getBot().getLogger().info("In DefenderOnGround leaf");
        //this ended up just being the entire looting leaf, doesn't really make a difference
        GroundItem defender = GroundItems.newQuery().names(defenders).unnoted().reachable().results().first();
        GroundItem desiredItem = GroundItems.newQuery().names(desired).unnoted().reachable().results().first();
        if(defender != null){
            if(defender.isVisible()) {
                if (defender.interact("Take")) {
                    Execution.delayUntil(() -> !defender.isValid(), () -> Players.getLocal().isMoving(), 200, 1000);
                }
            } else {
                Camera.turnTo(defender);
            }
        } else if (desiredItem != null){
            if(desiredItem.isVisible()) {
                if (desiredItem.interact("Take")) {
                    Execution.delayUntil(() -> !desiredItem.isValid(), () -> Players.getLocal().isMoving(), 200, 1000);
                }
            } else {
                Camera.turnTo(desiredItem);
            }
        }
    }
}
