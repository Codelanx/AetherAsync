package com.codelanx.aether.bots.defender.leaf;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.common.bot.AsyncExec;
import com.codelanx.aether.common.input.UserInput;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.script.Execution;

/**
 * NOTES:
 * 
 */
public class AnimateArmor implements Runnable {

    private DefenderBot bot;

    public AnimateArmor(DefenderBot bot) {this.bot = bot;}

    Npc targetingPlayer;

    @Override
    public void run() {
        Environment.getBot().getLogger().info("In AnimateArmor leaf");

        SpriteItem mithItem = Inventory.newQuery().names("Black full helm", "Black platebody", "Black platelegs").results().first();
        GameObject animator = GameObjects.newQuery().names("Magical Animator").results().nearest();

        if(animator != null){
            Environment.getBot().getLogger().info(animator.getDefinition().getName());
        }
        if(mithItem != null && animator != null) {
            UserInput.interact(mithItem, "Use").postAttempt().thenRun(() -> {
                if (!animator.isVisible()) {
                    Camera.turnTo(animator);
                }
                if (Inventory.getSelectedItem() != null && Inventory.getSelectedItem().getDefinition().getName().contains("Black")) {
                    UserInput.interact(animator, "Use").postAttempt().thenRun(() -> {
                        AsyncExec.delayUntil(() -> {
                            return (targetingPlayer = Npcs.newQuery().targeting(Players.getLocal()).reachable().results().nearest()) != null;
                        });
                    });
                }
            });
        }
    }
}
