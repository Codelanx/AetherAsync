package com.codelanx.aether.bots.smithing.branch;

import com.codelanx.aether.bots.smithing.BlastObject;
import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.AsyncExec;
import com.codelanx.aether.common.bot.Invalidators;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.input.UserInput;
import com.codelanx.aether.common.json.item.Material;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.codelanx.commons.logging.Logging;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.hud.interfaces.SpriteItem;
import com.runemate.game.api.script.framework.listeners.SkillListener;
import com.runemate.game.api.script.framework.listeners.events.SkillEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

//exclusively for goldsmith gauntlets, otherwise they're just always equipped
public class IceGlovesBarTask extends AetherTask<Integer> implements SkillListener {

    private static final Material ICE_GLOVES = Aether.getBot().getData().getItem("Ice gloves");
    private final AtomicBoolean xpGained = new AtomicBoolean();
    private final boolean gold;

    public IceGlovesBarTask(Recipe bar) {
        Aether.getBot().getEventDispatcher().addListener(this);
        this.gold = bar.getOutput().anyMatch(i -> i.getMaterial().getName().equalsIgnoreCase("gold bar"));
        if (!this.gold) {
            this.xpGained.set(true);
        }
        this.register(0, () -> {
            //interact with menu
            GameObject obj = Caches.forGameObject().get(BlastObject.BAR_DISPENSER).findAny().orElse(null);
            if (obj != null) {
                UserInput.interact(obj, "Search").postAttempt().thenRun(() -> {
                    //AsyncExec.
                });
            }
            if (this.gold) {
                this.xpGained.set(false);
            }
        });
        this.register(1, () -> {
            //swap gloves
            SpriteItem gloves = Caches.forInventory().get(ICE_GLOVES).findAny().orElse(null);
            if (gloves == null) {
                gloves = Caches.forEquipment().get(ICE_GLOVES).findAny().orElse(null);
                if (gloves == null) {
                    throw new IllegalStateException("Ice gloves task activated without ice gloves");
                }
                //shit, we were swapped around. Learn from it next time
                Logging.warning("Ice gloves equipped before goldsmith experience gained!");
            }
            //click component
        });
        this.register(2, Invalidators.SELF);

    }

    @Override
    public boolean isSync() {
        return true;
    }

    @Override
    public Supplier<Integer> getStateNow() {
        return () -> {
            return this.gold ? this.xpGained.get() ? 2 : 3 : 1;
        };
    }

    @Override
    public void onExperienceGained(SkillEvent $) {
        this.xpGained.set(true);
    }
}
