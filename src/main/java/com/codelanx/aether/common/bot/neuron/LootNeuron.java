package com.codelanx.aether.common.bot.neuron;

import com.codelanx.aether.common.bot.Brain;
import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.input.UserInput;
import com.codelanx.aether.common.json.item.Material;
import com.runemate.game.api.hybrid.entities.GroundItem;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.hybrid.util.calculations.Distance.Algorithm;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LootNeuron extends Neuron {

    private final Set<Material> pickup = new HashSet<>();

    //TODO: Account for expensive items on ground
    @Override
    public boolean applies() {
        return this.pickup.stream().anyMatch(i -> Caches.forGroundItems().get(i.toGroundItemInquiry()).count() > 0);
    }

    @Override
    public void fire(Brain brain) {
        Player p = Players.getLocal();
        Map<Double, GroundItem> items = this.pickup.stream().flatMap(i -> Caches.forGroundItems().get(i.toGroundItemInquiry())).collect(Collectors.toMap(g -> Distance.between(p, g, Algorithm.EUCLIDEAN_SQUARED), Function.identity()));
        //TODO: ge value comparator
        GroundItem closest = items.entrySet().stream().min(Comparator.comparing(Entry::getKey)).map(Entry::getValue).orElse(null);
        if (closest != null) {
            UserInput.interact(closest, "Take");
        }
    }

    @Override
    public boolean isBlocking() {
        return false;
    }

    @Override
    public boolean isEvaluationSkipped() {
        return this.pickup.isEmpty();
    }

    public boolean targetLoot(Material loot, boolean target) {
        return target ? this.pickup.add(loot) : this.pickup.remove(loot);
    }
}
