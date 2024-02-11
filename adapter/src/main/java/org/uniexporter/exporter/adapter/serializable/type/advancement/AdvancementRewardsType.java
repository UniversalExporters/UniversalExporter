package org.uniexporter.exporter.adapter.serializable.type.advancement;

import org.uniexporter.exporter.adapter.faces.Self;

import java.util.ArrayList;

public class AdvancementRewardsType implements Self<AdvancementRewardsType> {
    public int experience;
    public ArrayList<String> loots;
    public ArrayList<String> recipes;
    public LazyContainerType function;

    public AdvancementRewardsType experience(int experience) {
        this.experience = experience;
        return self();
    }

    public AdvancementRewardsType loots(String loot) {
        if (this.loots == null) loots = new ArrayList<>();
        loots.add(loot);
        return self();
    }

    public AdvancementRewardsType recipe(String recipe) {
        if (recipes == null) recipes = new ArrayList<>();
        recipes.add(recipe);
        return self();
    }

    public AdvancementRewardsType function(LazyContainerType function) {
        this.function = function;
        return self();
    }
}
