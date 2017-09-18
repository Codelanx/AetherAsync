package com.codelanx.aether.bots.smithing.branch;

import com.codelanx.aether.bots.smithing.BlastData;
import com.codelanx.aether.bots.smithing.BlastObject;
import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.input.UserInput;
import com.codelanx.aether.common.json.item.Material;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.codelanx.aether.common.rest.RestLoader;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;

import java.util.function.Supplier;

public class GetBarTask extends AetherTask<Boolean> {

    private final Recipe bar;

    public GetBarTask(Recipe bar) {
        this.bar = bar;
        this.register(true, () -> {
            //interact with it, then open menu
        });
        this.register(false, () -> {
            //use a bucket on it
            RestLoader loader = Aether.getBot().getData();
            Material bucket = loader.getItem("Water bucket");
            if (Caches.forInventory().get(bucket).anyMatch(Inventory.getSelectedItem()::equals)) {
                //click collector
                GameObject dispenser = this.getBarDispenser();
                UserInput.click(dispenser);
                return;
            }
            Caches.forInventory().get(bucket).findAny().ifPresent(UserInput::click);
        });
    }

    private GameObject getBarDispenser() {
        return Caches.forGameObject().get(BlastObject.BAR_DISPENSER).findAny().orElse(null);
    }

    //true if using ice gloves
    @Override
    public Supplier<Boolean> getStateNow() {
        //a & !(b & c), ice gloves NAND doing gold with smithy gauntlets
        return () -> BlastData.HAS_ICE_GLOVES.as(boolean.class) //gloves == no bucket
                && !(BlastData.HAS_GOLDSMITH_GAUNTLETS.as(boolean.class)
                    && bar.getOutput().anyMatch(i -> "Gold bar".equalsIgnoreCase(i.getMaterial().getName()))); //no gold == no bucket
    }
}
