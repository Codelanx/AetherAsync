package com.codelanx.aether.common.bot.mission;

import com.codelanx.aether.common.bot.AetherTaskWrapper;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.json.item.Material;
import com.runemate.game.api.hybrid.local.Skill;

import java.util.*;

public abstract class AetherMission<E> extends AetherTaskWrapper<E> {

    public AetherMission(AetherTask<E> root) {
        super(root);
    }

    public abstract boolean hasEnded();

    public static <T> AetherMission<T> of(AetherTask<T> root) {
        return new AetherMission<T>(root) {
            @Override
            public boolean hasEnded() {
                return false;
            }
        };
    }
    
    public static <T> AetherMissionBuilder<T> builder(AetherTask<T> root) {
        return new AetherMissionBuilder<>(root);
    }
    
    private static class AetherMissionBuilder<E> {
        
        private final AetherTask<E> root;
        private final Map<Skill, Integer> endLevels = new HashMap<>();
        private final Set<Skill> trackedSkills = new HashSet<>();
        private final Map<Material, Integer> maxGathered = new HashMap<>();
        private long runtimeMs;
        
        public AetherMissionBuilder(AetherTask<E> root) {
            this.root = root;
        }
        
        public AetherMissionBuilder targetSkills(Skill... skills) {
            this.trackedSkills.addAll(Arrays.asList(skills));
            return this;
        }
        
        public AetherMissionBuilder stopAtSkillLevel(Skill skill, int level) {
            this.endLevels.put(skill, level);
            return this;
        }
        
        //TODO
        public AetherMissionBuilder targetAmount(Material material, int amount) {
            this.maxGathered.put(material, amount);
            return this;
        }
        
        //TODO
        public AetherMissionBuilder runFor(long runtimeMs) {
            this.runtimeMs = runtimeMs;
            return this;
        }
        
        public AetherMission<E> build() {
            AetherMissionBuilder<E> amb = this;
            return new AetherMission<E>(this.root) {
                @Override
                public boolean hasEnded() {
                    Skill exceeded = amb.trackedSkills.stream().filter(s -> {
                        return amb.endLevels.getOrDefault(s, 100) <= s.getCurrentLevel();
                    }).findAny().orElse(null);
                    return exceeded != null;
                }
            };
        }
    }
}
