package org.uniexporter.exporter.adapter.serializable.type.itemAndBlock;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;
import org.uniexporter.exporter.adapter.faces.Self;

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
}
