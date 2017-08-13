package com.codelanx.aether.common.branch.sync;

import com.codelanx.aether.common.Actions;
import com.codelanx.aether.common.branch.sync.recipe.type.GoToFurnaceTask;
import com.codelanx.aether.common.branch.sync.recipe.type.GoToRangeTask;
import com.codelanx.commons.util.Reflections;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.details.Locatable;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.hybrid.util.calculations.Distance.Algorithm;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.framework.tree.LeafTask;

import java.util.function.Consumer;

public enum CommonActions implements Actions {

    MOVE_TO_RANGE(() -> walkTo(GoToRangeTask.findRange())),
    MOVE_TO_FURNACE(() -> walkTo(GoToFurnaceTask.findFurnace())),
    END(() -> Environment.getBot().stop()),
    WAIT(() -> Execution.delay(200)),
    ;

    private final Consumer<CommonActions> raw;
    private final LeafTask task;

    private CommonActions(Runnable run) {
        this((action) -> run.run());
    }

    private CommonActions(Consumer<CommonActions> run) {
        this.raw = run;
        this.task = new LeafTask() {
            @Override
            public void execute() {
                long start = System.currentTimeMillis();
                Environment.getLogger().info(CommonActions.this.name() + " action called: " + Reflections.getCaller());
                CommonActions.this.raw.accept(CommonActions.this);
                long diff = System.currentTimeMillis() - start;
                if (diff < 50) {
                    //edit: tickrate currently defined in LoopingBot, we need StateBot to manage
                    //Execution.delay(50 - diff); //we'll assume a 50ms tick, essentially
                }
            }
        };
    }

    private static void walkTo(Locatable locatable) {
        if (locatable != null) {
            double dist = Distance.between(locatable, Players.getLocal(), Algorithm.EUCLIDEAN_SQUARED);
            if (dist >= 5) {
                //TODO: Cache
                WebPath path = Traversal.getDefaultWeb().getPathBuilder().buildTo(locatable); //hopefully a single computation
                path.step();
            }
        }
    }

    @Override
    public LeafTask getTask() {
        return this.task;
    }
}
