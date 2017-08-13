package com.codelanx.aether.common.branch.async;

import com.codelanx.aether.common.bot.async.Invalidator;
import com.codelanx.aether.common.bot.async.task.AetherTask;
import com.codelanx.aether.common.branch.async.recipe.type.GoToFurnaceTask;
import com.codelanx.aether.common.branch.async.recipe.type.GoToRangeTask;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.details.Locatable;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.hybrid.util.calculations.Distance.Algorithm;

import java.util.function.Supplier;

public enum CommonTasks implements Supplier<Invalidator> {

    MOVE_TO_RANGE(() -> walkTo(GoToRangeTask.findRange())),
    MOVE_TO_FURNACE(() -> walkTo(GoToFurnaceTask.findFurnace())),
    END(() -> Environment.getBot().stop()),
    ;

    private final AetherTask<?> task;

    private CommonTasks(Runnable task) {
        this.task = AetherTask.of(task);
    }

    private CommonTasks(Supplier<Boolean> task) {
        this.task = AetherTask.ofRunemateFailable(task);
    }

    private static boolean walkTo(Locatable locatable) {
        if (locatable != null) {
            double dist = Distance.between(locatable, Players.getLocal(), Algorithm.EUCLIDEAN_SQUARED);
            if (dist >= 5) {
                //TODO: Cache
                WebPath path = Traversal.getDefaultWeb().getPathBuilder().buildTo(locatable); //hopefully a single computation
                path.step();
                return false;
            }
        }
        return true;
    }

    //TODO: Unfuck this
    @Override
    public Invalidator get() {
        return this.task.execute(null);
    }

    public AetherTask<?> asTask() {
        return this.task;
    }
}
