package com.codelanx.aether.bots.defender;

import com.codelanx.aether.bots.defender.branch.InFightingRooms;
import com.codelanx.aether.common.bot.AetherAsyncBot;
import com.codelanx.aether.common.bot.mission.AetherMission;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.script.framework.listeners.InventoryListener;
import com.runemate.game.api.script.framework.listeners.events.ItemEvent;

import java.io.File;

//Not for release, this is a conversion of a bot from snufalufugus (used for testing)
//previously: RegalWarriorGuild
public class DefenderBot extends AetherAsyncBot implements InventoryListener {

    private boolean newDefender;

    public DefenderBot() {
    }

    public void onStart(String... args) {
        super.onStart(args);
        getEventDispatcher().addListener(this);
        this.getBrain().register(AetherMission.of(new InFightingRooms(this)));
    }

    @Override
    public File getResourcePath() {
        return new File(super.getResourcePath(), "defender");
    }

    public Boolean getNewDefender() {
        return newDefender;
    }

    public void setNewDefender(Boolean defenderFlag) {
        newDefender = defenderFlag;
    }

    @Override
    public void onItemAdded(ItemEvent event) {
        Environment.getBot().getLogger().info("Item Added: " + event.getItem() + " (" + event.getQuantityChange() + ")");
        if (event.getItem().getDefinition() != null && event.getItem().getDefinition().getName().contains("defender")) {
            setNewDefender(true);
        }
    }

    @Override
    public void onItemRemoved(ItemEvent event) {
        Environment.getBot().getLogger().info("Item Removed: " + event.getItem() + " (" + event.getQuantityChange() + ")");
    }

}
