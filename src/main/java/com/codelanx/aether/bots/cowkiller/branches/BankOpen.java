package com.codelanx.aether.bots.cowkiller.branches;

import com.codelanx.aether.bots.cowkiller.CowKiller;
import com.codelanx.aether.bots.cowkiller.leaves.Walk;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.runemate.game.api.hybrid.local.hud.interfaces.Bank;

import java.util.function.Supplier;

public class BankOpen extends AetherTask<Boolean> {

    public BankOpen() {
        this.registerRunemateCall(true, Bank::close);
        this.register(false, new AtArea(CowKiller.COW_AREA, new CheckGround(), AetherTask.of(new Walk(CowKiller.COW_AREA))));
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return Bank::isOpen;
    }

}