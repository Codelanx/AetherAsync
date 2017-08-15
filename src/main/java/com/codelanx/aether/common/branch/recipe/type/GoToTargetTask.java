package com.codelanx.aether.common.branch.recipe.type;

import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.branch.recipe.TargetSelectTask;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.hybrid.util.calculations.Distance.Algorithm;

import java.util.function.Supplier;

public class GoToTargetTask<T extends LocatableEntity> extends AetherTask<Double> {

    private static final int INTERACTION_DISTANCE = 9;
    private final Supplier<T> target;

    public GoToTargetTask(Supplier<T> target, Recipe recipe) {
        this.target = target;
        this.registerRunemateCall(Double.NEGATIVE_INFINITY, () -> Camera.turnTo(GoToRangeTask.findRange()));
        this.register(d -> Math.abs(d) < INTERACTION_DISTANCE, new TargetSelectTask(recipe));
    }

    @Override
    public Supplier<Double> getStateNow() {
        return () -> {
            T obj = this.target.get();
            if (obj != null) {
                double dist = Distance.between(obj, Players.getLocal(), Algorithm.EUCLIDEAN_SQUARED);
                if (!obj.isVisible() && dist < INTERACTION_DISTANCE) {
                    return Double.NEGATIVE_INFINITY;
                }
            }
            return Double.POSITIVE_INFINITY;
        };
    }
}
