package org.uniexporter.exporter.adapter.serializable.type.itemAndBlock;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;
import org.uniexporter.exporter.adapter.faces.Self;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@SuppressWarnings("UnusedReturnValue")
public class ToolType extends ToolMaterialType implements Self<ToolType> {
    @SerializedName("tagId")
    public String tagId;

    @SerializedName("attackDamage")
    public float attackDamage;

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
        if (!(o instanceof ToolType)) return false;
        if (!super.equals(o)) return false;
        ToolType toolType = (ToolType) o;
        return Float.compare(attackDamage, toolType.attackDamage) == 0 && Objects.equals(tagId, toolType.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tagId, attackDamage);
    }
}
