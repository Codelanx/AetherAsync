package com.codelanx.aether.common.branch.gather;

import com.codelanx.aether.common.json.gather.Gather;
import com.codelanx.aether.common.json.recipe.Recipe;
import com.codelanx.commons.util.exception.Exceptions;
import com.runemate.game.api.hybrid.location.Area;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GatherOptions {

    private final Gather gather;
    private final Set<Recipe> disabled = new HashSet<>();
    private final Area area;

    private GatherOptions(GatherBuilder builder) {
        Exceptions.allNotNull(builder.gather, builder.area);
        this.gather = builder.gather;
        this.disabled.addAll(builder.disable);
        this.area = builder.area;
    }

    public Gather getData() {
        return this.gather;
    }

    public Set<Recipe> getDisabledRecipes() {
        return Collections.unmodifiableSet(this.disabled);
    }

    public Area getArea() {
        return this.area;
    }

    public static GatherBuilder builder() {
        return new GatherBuilder();
    }

    public static class GatherBuilder {

        private final Set<Recipe> disable = new HashSet<>();
        private Gather gather;
        private Area area;

        public GatherBuilder gather(Gather gather) {
            this.gather = gather;
            return this;
        }

        public GatherBuilder disableRecipe(Recipe recipe) {
            this.disable.add(recipe);
            return this;
        }

        public GatherBuilder area(Area area) {
            this.area = area;
            return this;
        }

        public GatherOptions build() {
            return new GatherOptions(this);
        }

        public GatherTask buildTask() {
            return new GatherTask(this.build());
        }
    }
}
