package com.codelanx.aether.common.branch.bank;

import com.codelanx.aether.bots.cooking.CookingBot;
import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Landmark;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPath;
import com.runemate.game.api.hybrid.location.navigation.web.WebPathBuilder;
import com.runemate.game.api.hybrid.region.Banks;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.hybrid.util.calculations.Distance.Algorithm;

import java.util.function.Function;
import java.util.function.Supplier;

//walks to bank, and withdraws
public class BankTask extends AetherTask<Boolean> {

    public BankTask(Recipe recipe) {
        this.register(true, new BankOpenTask(recipe));
        this.register(false, () -> {
            //TODO: Cache
            Function<WebPathBuilder, Path> pather = Aether.getBot() instanceof CookingBot
                    ? b -> b.buildTo(new Coordinate(2809, 3441, 0))
                    : b -> b.buildTo(Landmark.BANK);
            Path path = pather.apply(Traversal.getDefaultWeb().getPathBuilder());
            if (path == null) {
                if (Aether.getBot() instanceof CookingBot) {
                    path = RegionPath.buildTo(new Coordinate(2809, 3441, 0));
                    if (path == null) {
                        Aether.getBot().stop();
                        Environment.getLogger().severe("Could not path to bank");
                        return;
                    }
                } else {
                    Aether.getBot().stop();
                    Environment.getLogger().severe("Could not path to bank");
                    return;
                }
            }
            path.step();
        });
    }

    @Override
    public Supplier<Boolean> getStateNow() {
        GameObject booth = Banks.getLoadedBankBooths().nearest();
        return () -> booth != null && booth.isVisible() && Distance.between(booth, Players.getLocal(), Algorithm.EUCLIDEAN_SQUARED) < 16;
    }

}
