package com.codelanx.aether.bots.smithing.branch;

import com.codelanx.aether.bots.smithing.BlastData;
import com.codelanx.aether.bots.smithing.BlastObject;
import com.codelanx.aether.bots.smithing.branch.BlastFurnaceTask.BlastState;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.branch.GoToTargetTask;
import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.input.UserInput;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.runemate.game.api.hybrid.entities.GameObject;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

//this class assumes successful completion of each task
public class BlastFurnaceTask extends AetherTask<BlastState> {

    private final AtomicReference<BlastState> state = new AtomicReference<>();

    public BlastFurnaceTask(Recipe bar) {
        this.state.set(BlastState.BANKING);
        this.register(BlastState.BANKING, new BlastBankTask(bar));
        Supplier<Optional<GameObject>> getObj = () -> Caches.forGameObject().get(BlastObject.BELT).findAny();
        this.register(BlastState.PLACE_ORES, new GoToTargetTask<>(() -> getObj.get().orElse(null), AetherTask.of(() -> {
            if (BlastData.CARRYING_COAL.as(boolean.class)) {
                BlastData.COAL_IN_FURNACE.set(BlastData.COAL_IN_FURNACE.as(int.class) + BlastData.ORE_PER_TRIP);
                BlastData.CARRYING_COAL.set(false);
            }
            getObj.get().ifPresent(UserInput::click);
        }))); //raw coal
        this.register(BlastState.FILL_BUCKET, () -> Caches.forGameObject().get(BlastObject.SINK).findAny().ifPresent(o -> UserInput.interact(o, "Fill-bucket")));
        this.register(BlastState.GETTING_BARS, new GoToTargetTask<>(BlastObject.BAR_DISPENSER, new GetBarTask(bar)));
    }

    public enum BlastState {
        BANKING,
        GETTING_BARS,
        FILL_BUCKET,
        PLACE_ORES,
        ;

        public BlastState getNextState() {
            switch (this) {
                case PLACE_ORES:
                    return BlastData.BARS_IN_FURNACE.as(int.class) >= BlastData.ORE_RECLAIM_LIMIT ? GETTING_BARS : BANKING;
                case GETTING_BARS:
                    return FILL_BUCKET;
                case FILL_BUCKET:
                    return BANKING;
                case BANKING:
                    return PLACE_ORES;
                default:
                    return BANKING;
            }
        }
    }

    @Override
    protected void onInvalidate() {
        this.state.updateAndGet(BlastState::getNextState);
    }

    @Override
    public Supplier<BlastState> getStateNow() {
        return this.state::get;
    }

}
