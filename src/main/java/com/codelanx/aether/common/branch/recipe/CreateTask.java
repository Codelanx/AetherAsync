package com.codelanx.aether.common.branch.recipe;

import com.codelanx.aether.bots.fletching.FletchingBot;
import com.codelanx.aether.common.Leveling;
import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.AsyncExec;
import com.codelanx.aether.common.bot.Invalidator;
import com.codelanx.aether.common.bot.Invalidators;
import com.codelanx.aether.common.input.UserInput;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceContainer;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceContainers;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.script.Execution;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CreateTask implements Supplier<Invalidator> {

    private final Recipe recipe;

    public CreateTask(Recipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public Invalidator get() {
        Environment.getLogger().info("\t\t=>CreateTask");
        InterfaceContainer cont;
        SpriteItem item;
        switch (this.recipe.getRecipeType()) {
            case COOK:
                if (!InterfaceContainers.isLoaded(this.recipe.getContainerId())) {
                    Execution.delay(100);
                    return Invalidators.NONE;
                }
                cont = InterfaceContainers.getAt(this.recipe.getContainerId());
                UserInput.interact(cont.getComponents(i -> true).random(), "Cook All");
                break;
            case SMELT:
                if (!InterfaceContainers.isLoaded(this.recipe.getContainerId())) {
                    Execution.delay(100);
                    return Invalidators.NONE;
                }
                cont = InterfaceContainers.getAt(this.recipe.getContainerId());
                UserInput.interact(cont.getComponents(i -> true).random(), "Smelt All");
                break;
            case CLICK: //TODO
                return Invalidators.ALL;
            case COMBINE: 
                if (!InterfaceContainers.isLoaded(this.recipe.getContainerId())) {
                    Execution.delay(100);
                    return Invalidators.NONE;
                }
                cont = InterfaceContainers.getAt(this.recipe.getContainerId());
                UserInput.interact(cont.getComponents(i -> true).random(), "Make 10 sets").postAttempt().thenRun(() -> {
                    if (Aether.getBot() instanceof FletchingBot) {
                        int blevel = Skill.FLETCHING.getBaseLevel();
                        long curr = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(12);
                        AsyncExec.delayUntil(() -> {
                            //TODO: Proper check, this works for fletching only (bandaid)
                            return curr < System.currentTimeMillis() || Skill.FLETCHING.getBaseLevel() > blevel;
                        });
                    }
                });
                
        }
        return Invalidators.ALL;
    }
}
