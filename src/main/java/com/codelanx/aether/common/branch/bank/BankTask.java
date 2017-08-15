package com.codelanx.aether.common.branch.bank;

import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.location.navigation.Landmark;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.region.Banks;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.hybrid.util.calculations.Distance.Algorithm;

import java.util.function.Supplier;

//walks to bank, and withdraws
public class BankTask extends AetherTask<Boolean> {

    public BankTask(Recipe recipe) {
        this.register(true, new BankOpenTask(recipe));
        this.registerRunemateCall(false, () -> {
            //TODO: Cache
            WebPath path = Traversal.getDefaultWeb().getPathBuilder().buildTo(Landmark.BANK);
            path.step();
            return true;
        });
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        GameObject booth = Banks.getLoadedBankBooths().nearest();
        return () -> booth != null && booth.isVisible() && Distance.between(booth, Players.getLocal(), Algorithm.EUCLIDEAN_SQUARED) < 16;
    }

}
