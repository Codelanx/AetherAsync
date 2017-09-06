package com.codelanx.aether.bots.cowkiller.branches;

import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.region.Players;

import java.util.function.Supplier;

public class AtArea extends AetherTask<Boolean> {

    private final Area area;

    public AtArea(Area area, AetherTask<?> success, AetherTask<?> failure) {
        this.area = area;
        this.register(true, success);
        this.register(false, failure);
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> {
            Player player = Players.getLocal();
            return player != null && this.area.contains(player);
        };
    }

}
