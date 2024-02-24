package org.uniexporter.exporter.adapter.serializable;

import com.google.gson.annotations.SerializedName;
import org.uniexporter.exporter.adapter.faces.Self;
import org.uniexporter.exporter.adapter.serializable.type.NameType;
import org.uniexporter.exporter.adapter.serializable.type.advancement.AdvancementCriterionType;
import org.uniexporter.exporter.adapter.serializable.type.advancement.AdvancementDisplayType;
import org.uniexporter.exporter.adapter.serializable.type.advancement.AdvancementRewardsType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class AdvancementSerializable extends NameType implements Self<AdvancementSerializable> {
    @SerializedName("children")
    public Advancements children;
    @SerializedName("display")
    public AdvancementDisplayType display;
    @SerializedName("rewards")
    public AdvancementRewardsType rewards;

    @SerializedName("criteria")
    public ConcurrentHashMap<String, AdvancementCriterionType> criteria;
    @SerializedName("requirements")
    public ArrayList<ArrayList<String>> requirements;
    @SerializedName("sendsTelemetryEvent")
    public boolean sendsTelemetryEvent;

    public static AdvancementSerializable advancement(Consumer<AdvancementSerializable> consumer) {
        AdvancementSerializable advancementSerializable = new AdvancementSerializable();
        consumer.accept(advancementSerializable);
        return advancementSerializable;
    }

    public AdvancementSerializable sendsTelemetryEvent(boolean sendsTelemetryEvent) {
        this.sendsTelemetryEvent = sendsTelemetryEvent;
        return self();
    }

    public AdvancementSerializable requirements(String... requirement) {
        if (this.requirements == null) requirements = new ArrayList<>();
        requirements.add(new ArrayList<>(Arrays.asList(requirement)));
        return self();
    }

    public AdvancementSerializable criteria(String key, AdvancementCriterionType criteria) {
        if (this.criteria == null) this.criteria = new ConcurrentHashMap<>();
        this.criteria.put(key, criteria);
        return self();
    }

    public AdvancementSerializable name(String name) {
        this.name = name;
        return self();
    }

    public AdvancementSerializable englishName(String englishName) {
        this.englishName = englishName;
        return self();
    }

    public AdvancementSerializable rewards(AdvancementRewardsType rewards) {
        this.rewards = rewards;
        return self();
    }

    public AdvancementSerializable display(AdvancementDisplayType display) {
        this.display = display;
        return self();
    }

    public AdvancementSerializable children(Consumer<Advancements> consumer) {
        if (children == null) children = new Advancements();
        consumer.accept(children);
        return self();
    }
}
