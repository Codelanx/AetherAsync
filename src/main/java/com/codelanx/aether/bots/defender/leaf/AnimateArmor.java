package com.codelanx.aether.bots.defender.leaf;

import com.codelanx.aether.bots.defender.DefenderBot;
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

        SpriteItem mithItem = Inventory.newQuery().names("Mithril full helm", "Mithril platebody", "Mithril platelegs").results().first();
        GameObject animator = GameObjects.newQuery().names("Magical Animator").results().nearest();

        if(animator != null){
            Environment.getBot().getLogger().info(animator.getDefinition().getName());
        }
        if(mithItem != null && animator != null){
            mithItem.interact("Use");
            if(Inventory.getSelectedItem() != null && Inventory.getSelectedItem().getDefinition().getName().contains("Mithril")){
                if(animator.isVisible()){
                    if(animator.interact("Use")) {
                        Execution.delayUntil(() -> (targetingPlayer = Npcs.newQuery().targeting(Players.getLocal()).reachable().results().nearest()) != null, 2000, 20000);
                    }
                } else {
                    Camera.turnTo(animator);
                }
            }
        }
    }
}
