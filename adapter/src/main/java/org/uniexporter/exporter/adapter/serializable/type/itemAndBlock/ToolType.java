package org.uniexporter.exporter.adapter.serializable.type.itemAndBlock;

import org.jetbrains.annotations.Nullable;
import org.uniexporter.exporter.adapter.faces.Self;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@SuppressWarnings("UnusedReturnValue")
public class ToolType implements Self<ToolType> {
    public String tagId;
    public float miningSpeed;
    public float miningLevel;
    public float attackDamage;
    public int enchantability;
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
