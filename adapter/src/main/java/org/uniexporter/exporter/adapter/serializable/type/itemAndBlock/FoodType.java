package org.uniexporter.exporter.adapter.serializable.type.itemAndBlock;

import com.google.gson.annotations.SerializedName;
import org.uniexporter.exporter.adapter.serializable.type.status.StatusEffectInstanceType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class FoodType {
    @SerializedName("hunger")
    public int hunger;
     @SerializedName("saturationModifier")
    public float saturationModifier;
     @SerializedName("meat")
    public boolean meat;
     @SerializedName("alwaysEdible")
    public boolean alwaysEdible;
     @SerializedName("snack")
    public boolean snack;
     @SerializedName("statusEffects")
    public ConcurrentHashMap<StatusEffectInstanceType, Float> statusEffects;

    public static FoodType foodType(Consumer<FoodType> consumer) {
        FoodType foodType = new FoodType();
        consumer.accept(foodType);
        return foodType;
    }

    public void statusEffects(StatusEffectInstanceType type, Float f) {
        if (this.statusEffects == null) this.statusEffects = new ConcurrentHashMap<>();
        statusEffects.put(type, f);
    }
}
