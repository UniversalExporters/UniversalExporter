package org.uniexporter.exporter.adapter.serializable.type.itemAndBlock;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ToolMaterialType {
    @SerializedName("miningSpeed")
    public float miningSpeed;

    @SerializedName("miningLevel")
    public float miningLevel;

    @SerializedName("enchantability")
    public int enchantability;
    @SerializedName("repairIngredients")
    public ConcurrentHashMap<String, NbtType> repairIngredients;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToolMaterialType that = (ToolMaterialType) o;
        return Float.compare(miningSpeed, that.miningSpeed) == 0 && Float.compare(miningLevel, that.miningLevel) == 0 && enchantability == that.enchantability && Objects.equals(repairIngredients, that.repairIngredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(miningSpeed, miningLevel, enchantability, repairIngredients);
    }
}
