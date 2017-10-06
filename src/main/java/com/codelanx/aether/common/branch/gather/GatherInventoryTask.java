package com.codelanx.aether.common.branch.gather;

import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.json.gather.Gather;

import java.util.function.Supplier;

public class GatherInventoryTask extends AetherTask<Boolean> {

    private final GatherOptions gather;

    public GatherInventoryTask(GatherOptions gather) {
        this.gather = gather;
    }


    @Override
    public Supplier<Boolean> getStateNow() {
        return () -> {
            if (this.gather.getData().hasRecipes()) {
                //utilize possible recipes
            }
            return false; //TODO
        };
    }
}
