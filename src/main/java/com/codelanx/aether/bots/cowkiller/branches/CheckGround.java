package com.codelanx.aether.bots.cowkiller.branches;

import com.codelanx.aether.bots.cowkiller.CowKiller;
import com.codelanx.aether.bots.cowkiller.leaves.AttackCow;
import com.codelanx.aether.bots.cowkiller.leaves.LootHide;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.region.GroundItems;

import java.util.function.Supplier;

public class CheckGround extends AetherTask<Boolean> {

    public CheckGround() {
        this.register(true, new AttackCow());
        this.register(false, new LootHide());
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> GroundItems.newQuery().names("Cowhide").within(CowKiller.COW_AREA).results().isEmpty();
    }

}