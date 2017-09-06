package com.codelanx.aether.bots.cowkiller.branches;

import com.codelanx.aether.bots.cowkiller.CowKiller;
import com.codelanx.aether.bots.cowkiller.leaves.TanHides;
import com.codelanx.aether.bots.cowkiller.leaves.Walk;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;

import java.util.function.Supplier;

public class ContainsCowhide extends AetherTask<Boolean> {

    public ContainsCowhide() {
        this.register(true, new AtArea(CowKiller.JACK_AREA, AetherTask.of(new TanHides()), AetherTask.of(new Walk(CowKiller.JACK_AREA))));
        this.register(false, new AtArea(CowKiller.BANK_AREA, new BankDeposit(), AetherTask.of(new Walk(CowKiller.BANK_AREA))));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> Inventory.contains("Cowhide");
    }

}