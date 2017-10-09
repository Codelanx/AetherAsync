package com.codelanx.aether.common.branch.recipe;

import com.codelanx.aether.bots.fletching.FletchingBot;
import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.AsyncExec;
import com.codelanx.aether.common.bot.Invalidator;
import com.codelanx.aether.common.bot.Invalidators;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.bot.task.ExecutableAetherTask;
import com.codelanx.aether.common.input.UserInput;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.codelanx.commons.logging.Logging;
import com.codelanx.commons.util.Scheduler;
import com.runemate.game.api.hybrid.input.Keyboard;
import com.runemate.game.api.hybrid.local.Skill;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceComponent;
import com.runemate.game.api.hybrid.local.hud.interfaces.InterfaceContainers;
import com.runemate.game.api.script.Execution;
import com.runemate.game.api.script.data.Category;
import com.runemate.game.api.script.framework.listeners.SkillListener;
import com.runemate.game.api.script.framework.listeners.events.SkillEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CreateTask extends AetherTask<Void> implements SkillListener {

    private static final Pattern MAKE_10_PATTERN = Pattern.compile("Make 10( sets)?");
    private static final Pattern MAKE_X_PATTERN = Pattern.compile("Make X( sets)?");
    private final Map<Skill, AtomicInteger> expectedGains = new HashMap<>();
    private final AtomicLong lastUpdate = new AtomicLong();
    private final Recipe recipe;

    public CreateTask(Recipe recipe) {
        this.recipe = recipe;
        Aether.getBot().getEventDispatcher().addListener(this);
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    @Override
    public boolean isSync() {
        return true;
    }

    @Override
    public Invalidator execute(Void state) {
        Logging.info("\t\t=>CreateTask");
        if (!InterfaceContainers.isLoaded(this.recipe.getContainerId())) {
            Execution.delay(100);
            return Invalidators.NONE;
        }
        InterfaceComponent component = this.recipe.queryInputComponent();
        if (component == null) {
            throw new NoSuchElementException("Could not locate component within loaded container: " + this.recipe.componentInquiry());
        }
        if (!component.isVisible()) {
            throw new IllegalStateException("Can not click an invisible component");
        }
        int rem = this.recipe.getRemainder();
        Skill type = this.inferrType();
        switch (this.recipe.getRecipeType()) {
            case COOK:
                UserInput.interact(component, "Cook All");
                break;
            case SMELT:
                UserInput.interact(component, "Smelt All");
                break;
            case CLICK: //TODO
                return Invalidators.ALL;
            case COMBINE:
                UserInput.runemateInput(() -> {
                    if (component.interact("Make All")) {
                        return true;
                    } else if (component.interact(MAKE_X_PATTERN)) {
                        //TODO: Seems this is failing
                        //wait for component
                        Execution.delayUntil(() -> {
                            if (!InterfaceContainers.isLoaded(162)) {
                                return false;
                            }
                            InterfaceComponent comp = InterfaceContainers.getAt(162).getComponent(33);
                            if (comp == null || !comp.isVisible()) {
                                return false;
                            }
                            return "Enter amount:".equals(comp.getText());
                        });//.thenRun(() -> UserInput.type("99", true));
                        //enter 99 TODO: Randomize
                        UserInput.type("99", true);
                        return true;
                    }
                    this.expectedGains.clear(); //deferr to lastUpdate
                    return component.interact(MAKE_10_PATTERN);
                });

        }
        this.expectedGains.compute(type, (k, old) -> new AtomicInteger(rem + (old == null ? 0 : old.get())));
        AsyncExec.delayUntil(() -> {
            boolean timeout = this.lastUpdate.get() + TimeUnit.SECONDS.toMillis(3) < System.currentTimeMillis();
            boolean fullGains = this.expectedGains.values().stream().map(AtomicInteger::get).anyMatch(i -> i <= 0);
            Logging.info("CreateTask timeout: (" + timeout + " || " + fullGains + ")");
            return timeout || fullGains;
        });
        return Invalidators.ALL;
    }

    @Override
    protected void onInvalidate() {
        super.onInvalidate();
        this.expectedGains.clear();
    }

    //TODO: Make this based on something else, like in-recipe xp tracking
    private Skill inferrType() {
        List<Category> categoryList = Aether.getBot().getMetaData().getCategories();
        Skill back;
        switch (this.recipe.getRecipeType()) {
            case COOK:
                back = Skill.COOKING;
                break;
            case SMELT:
                back = Skill.SMITHING;
                break;
            default:
                if (categoryList.isEmpty()) {
                    Logging.warning("Could not inferr skill type for bot, no supplied categories");
                    return null;
                }
                back = categoryList.stream().map(c -> {
                    try {
                        return Skill.valueOf(c.name());
                    } catch (IllegalArgumentException ex) {
                        return null;
                    }
                }).filter(Objects::nonNull).findAny().orElse(null);
                if (back == null) {
                    Logging.warning("Could not inferr skill type for bot, mismatched names?");
                }
        }
        return back;
    }

    @Override
    public void onExperienceGained(SkillEvent event) {
        this.handle(event, AtomicInteger::decrementAndGet);
    }

    @Override
    public void onLevelUp(SkillEvent event) {
        this.handle(event, i -> i.set(0));
    }

    private void handle(SkillEvent event, Consumer<AtomicInteger> operation) {
        Stream.of(event.getSkill(), null).forEach(s -> {
            this.expectedGains.computeIfPresent(s, (k, old) -> {
                operation.accept(old);
                return old;
            });
        });
        this.lastUpdate.set(System.currentTimeMillis());
    }

    @Override
    public Supplier<Void> getStateNow() {
        return () -> null;
    }
}
