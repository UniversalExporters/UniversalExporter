package org.uniexporter.exporter.adapter.serializable.type.itemAndBlock;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;
import org.uniexporter.exporter.adapter.faces.Self;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@SuppressWarnings("UnusedReturnValue")
public class ToolType implements Self<ToolType> {
    @SerializedName("tagId")
    public String tagId;
    @SerializedName("miningSpeed")
    public float miningSpeed;
    @SerializedName("miningLevel")
    public float miningLevel;
    @SerializedName("attackDamage")
    public float attackDamage;
    @SerializedName("enchantability")
    public int enchantability;
    @SerializedName("repairIngredients")
    public ConcurrentHashMap<String, NbtType> repairIngredients;

    public ToolType repairIngredient(String registerName, @Nullable NbtType nbt) {
        if (this.repairIngredients == null) this.repairIngredients = new ConcurrentHashMap<>();
        return self();
    }

    public static ToolType toolType(Consumer<ToolType> consumer) {
        ToolType toolType = new ToolType();
        consumer.accept(toolType);
        return toolType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToolType toolType = (ToolType) o;
        return Float.compare(miningSpeed, toolType.miningSpeed) == 0 && Float.compare(miningLevel, toolType.miningLevel) == 0 && Float.compare(attackDamage, toolType.attackDamage) == 0 && enchantability == toolType.enchantability && Objects.equals(tagId, toolType.tagId) && Objects.equals(repairIngredients, toolType.repairIngredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagId, miningSpeed, miningLevel, attackDamage, enchantability, repairIngredients);
    }
}
