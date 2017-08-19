package com.codelanx.aether.bots.defender.leaf;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.AsyncExec;
import com.codelanx.aether.common.input.UserInput;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.region.GroundItems;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;

import java.util.Arrays;

/**
 * NOTES:
 * Pick up defender and set the defender flag true
 */
public class DefenderOnGround implements Runnable {

    private DefenderBot bot;

    public DefenderOnGround(DefenderBot bot) {this.bot = bot;}

    private static final String defenders[] = {"Bronze defender", "Iron defender", "Steel defender", "Black defender", "Mithril defender", "Adamant defender", "Rune defender", "Dragon defender"};
    private static final String desired[] = {"Black full helm", "Warrior guild token", "Black platebody", "Black platelegs", "Bronze defender", "Iron defender", "Steel defender", "Black defender", "Mithril defender", "Adamant defender", "Rune defender", "Dragon defender"};

    @Override
    public void run() {
        Aether.getBot().getLogger().info("In DefenderOnGround leaf");
        //this ended up just being the entire looting leaf, doesn't really make a difference
        GroundItem defender = GroundItems.newQuery().names(defenders).unnoted().reachable().results().first();
        GroundItem desiredItem = GroundItems.newQuery().names(desired).unnoted().reachable().results().first();
        for (GroundItem i : Arrays.asList(defender, desiredItem)) {
            if (i != null) {
                if (i.isVisible()) {
                    UserInput.interact(i, "Take").postAttempt().thenRun(() -> {
                        AsyncExec.delayUntil(() -> !i.isValid() || Players.getLocal().isMoving());
                    });
                } else {
                    Camera.turnTo(i);
                }
                break;
            }
        }
    }
}
