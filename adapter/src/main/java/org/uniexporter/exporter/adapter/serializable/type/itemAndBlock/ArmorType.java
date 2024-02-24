package org.uniexporter.exporter.adapter.serializable.type.itemAndBlock;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;
import org.uniexporter.exporter.adapter.faces.Self;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ArmorType implements Self<ArmorType> {
    @SerializedName("type")
    public String type;
    @SerializedName("equipmentSlot")
    public String equipmentSlot;
    @SerializedName("enchantability")
    public int enchantability;
    @SerializedName("protection")
    public int protection;
    @SerializedName("toughness")
    public float toughness;
    @SerializedName("knockbackResistance")
    public float knockbackResistance;

    @SerializedName("repairIngredients")
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
