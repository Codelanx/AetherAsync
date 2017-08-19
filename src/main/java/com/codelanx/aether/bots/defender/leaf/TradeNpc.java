package com.codelanx.aether.bots.defender.leaf;

import com.codelanx.aether.bots.defender.DefenderBot;
import com.codelanx.aether.common.bot.AsyncExec;
import com.codelanx.aether.common.input.UserInput;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.local.hud.interfaces.Shop;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.script.Execution;

/**
 * NOTES:
 * 
 */
public class TradeNpc implements Runnable {

    private DefenderBot bot;

    public TradeNpc(DefenderBot bot) {this.bot = bot;}

    @Override
    public void run() {
        Npc shopNPC = Npcs.newQuery().names("Lidio").reachable().results().nearest();
        if(shopNPC != null){
            if(shopNPC.isVisible()) {
                UserInput.interact(shopNPC, "Trade").postAttempt().thenRun(() -> {
                    AsyncExec.delayUntil(Shop::isOpen);
                });
            } else {
                Camera.turnTo(shopNPC);
            }
        }
    }
}
