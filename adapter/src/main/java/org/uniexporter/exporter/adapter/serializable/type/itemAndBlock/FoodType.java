package org.uniexporter.exporter.adapter.serializable.type.itemAndBlock;

import java.util.concurrent.ConcurrentHashMap;

public class FoodType {
    public int hunger;
    public float saturationModifier;
    public boolean meat;
    public boolean alwaysEdible;
    public boolean snack;
    public ConcurrentHashMap<StatusEffectInstanceType, Float> statusEffects;
}
