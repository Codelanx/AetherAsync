package com.codelanx.aether.common.branch.recipe;

import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.branch.recipe.TargetSelectTask;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.details.Locatable;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.hybrid.util.calculations.Distance.Algorithm;

import java.util.function.Supplier;

public class GoToTargetTask<T extends LocatableEntity> extends AetherTask<Double> {

    private static final int INTERACTION_DISTANCE = 9;
    private final Supplier<T> target;

    public GoToTargetTask(Supplier<T> target, Recipe recipe) {
        this.target = target;
        this.registerRunemateCall(Double.NEGATIVE_INFINITY, () -> Camera.turnTo(this.target.get()));
        this.register(d -> Math.abs(d) < INTERACTION_DISTANCE, new TargetSelectTask(recipe));
        this.registerDefault(AetherTask.ofRunemateFailable(new MoveToTargetTask(target)));
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
    
    private static class MoveToTargetTask implements Supplier<Boolean> {

        private final Supplier<? extends Locatable> target;

        public MoveToTargetTask(Supplier<? extends Locatable> target) {
            this.target = target;
        }

        @Override
        public Boolean get() {
            Locatable locatable = this.target.get();
            if (locatable != null) {
                double dist = Distance.between(locatable, Players.getLocal(), Distance.Algorithm.EUCLIDEAN_SQUARED);
                if (dist >= 5) {
                    //TODO: Cache
                    //RegionPath path = RegionPath.buildTo(locatable);
                    WebPath path = Traversal.getDefaultWeb().getPathBuilder().buildTo(locatable); //hopefully a single computation
                    path.step();
                    return false;
                }
            }
            return true;
        }
    }
}
