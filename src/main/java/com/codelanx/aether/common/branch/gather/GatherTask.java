package com.codelanx.aether.common.branch.gather;

import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.branch.BiTask;
import com.codelanx.aether.common.branch.GoToTargetTask;
import com.codelanx.aether.common.cache.Queryable;
import com.runemate.game.api.hybrid.entities.details.Interactable;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GatherTask extends AetherTask<Boolean> {

    public GatherTask(GatherOptions gather) {
        //allow multi-target
        Set<Interactable> ints = gather.getData().getTargets().map(Queryable::queryGlobal).flatMap(Function.identity()).collect(Collectors.toSet());
        //GoToTargetTask<?, ?> fisher = new GoToTargetTask<>());
        //this.register(true, new GoToTargetTask<>(() -> area, ));
        //this.register(true, new BiTask());
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        return null;
    }
}
