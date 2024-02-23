package org.uniexporter.exporter.adapter.serializable.type.itemAndBlock;

import org.jetbrains.annotations.Nullable;
import org.uniexporter.exporter.adapter.faces.Self;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ArmorType implements Self<ArmorType> {
    public String type;
    public String equipmentSlot;
    public int enchantability;
    public int protection;
    public float toughness;
    public float knockbackResistance;

    public ConcurrentHashMap<String, NbtType> repairIngredients;

    public ArmorType repairIngredient(String registerName, @Nullable NbtType nbt) {
        if (this.repairIngredients == null) this.repairIngredients = new ConcurrentHashMap<>();
        return self();
    }

    public static ArmorType armorType(Consumer<ArmorType> consumer) {
        ArmorType armorType = new ArmorType();
        consumer.accept(armorType);
        return armorType;
    }

}
