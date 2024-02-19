package org.uniexporter.exporter.adapter.serializable.type.itemAndBlock;

import org.uniexporter.exporter.adapter.serializable.type.status.StatusEffectInstanceType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class FoodType {
    public int hunger;
    public float saturationModifier;
    public boolean meat;
    public boolean alwaysEdible;
    public boolean snack;
    public ConcurrentHashMap<StatusEffectInstanceType, Float> statusEffects;

    public static FoodType foodType(Consumer<FoodType> consumer) {
        FoodType foodType = new FoodType();
        consumer.accept(foodType);
        return foodType;
    }
}
