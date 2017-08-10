package com.codelanx.aether.construction;

import com.codelanx.aether.common.Actions;
import com.codelanx.commons.util.Reflections;
import com.codelanx.aether.construction.mission.branch.bank.ButlerSpeakTask;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.local.House;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.hybrid.util.calculations.Distance.Algorithm;
import com.runemate.game.api.rs3.local.hud.Powers.Magic;
import com.runemate.game.api.script.framework.tree.LeafTask;

import java.util.function.Consumer;

public enum ConstructionActions implements Actions {

    CALL_BUTLER((action) -> {
        House.Options.callServant();
    }),
    HOUSE_TELEPORT(Magic.HOUSE_TELEPORT::activate),
    MOVE_TO_BUTLER((action) -> {
        Npc npc = ButlerSpeakTask.findButler();
        if (npc != null) {
            double dist = Distance.between(npc, Players.getLocal(), Algorithm.EUCLIDEAN_SQUARED);
            if (dist < 5) {
                npc.click();
            } else if (dist > 16) {
                ConstructionActions.CALL_BUTLER.getTask().execute();
            } else {
                RegionPath path = RegionPath.buildTo(npc); //hopefully a single computation
                path.step();
            }
        } else {
            ConstructionActions.CALL_BUTLER.getTask().execute();
        }
    }),
    ;

    private final Consumer<ConstructionActions> raw;
    private final LeafTask task;

    private ConstructionActions(Runnable run) {
        this((action) -> run.run());
    }

    private ConstructionActions(Consumer<ConstructionActions> run) {
        this.raw = run;
        this.task = new LeafTask() {
            @Override
            public void execute() {
                long start = System.currentTimeMillis();
                Environment.getLogger().info(ConstructionActions.this.name() + " action called: " + Reflections.getCaller());
                ConstructionActions.this.raw.accept(ConstructionActions.this);
                long diff = System.currentTimeMillis() - start;
                if (diff < 50) {
                    //edit: tickrate currently defined in LoopingBot, we need StateBot to manage
                    //Execution.delay(50 - diff); //we'll assume a 50ms tick, essentially
                }
            }
        };
    }

    @Override
    public LeafTask getTask() {
        return this.task;
    }
}
