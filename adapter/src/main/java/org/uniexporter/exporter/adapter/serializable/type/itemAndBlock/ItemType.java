package org.uniexporter.exporter.adapter.serializable.type.itemAndBlock;

import com.google.gson.annotations.SerializedName;
import org.uniexporter.exporter.adapter.annotations.AdvancementParameters;
import org.uniexporter.exporter.adapter.faces.Self;
import org.uniexporter.exporter.adapter.serializable.type.IconType;
import org.uniexporter.exporter.adapter.serializable.type.NameType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ItemType implements Self<ItemType> {
    @SerializedName("tab")
    public NameType tab;
    @SerializedName("maxStackSize")
    public int maxStackSize;
    @SerializedName("maxDurability")
    public int maxDurability;
    @SerializedName("OredictList")
    public ArrayList<String> OredictList;
    @SerializedName("icon")
    public IconType icon;
    @SerializedName("asBlock")
    public BlockType asBlock;
    @SerializedName("asFluid")
    public String asFluid;
    @SerializedName("maxUseTime")
    public Integer maxUseTime;
    @SerializedName("asFood")
    public FoodType asFood;
    @SerializedName("tool")
    public ToolType tool;
    @SerializedName("armor")
    public ArmorType armor;
    @SerializedName("fuelTime")
    public int fuelTime;
    @SerializedName("nbt")
    public NbtType nbt;

    @SerializedName("asEgg")
    public SpawnType asEgg;

    public static ItemType itemType(Consumer<ItemType> consumer) {
        ItemType itemType = new ItemType();
        consumer.accept(itemType);
        return itemType;
    }

    @AdvancementParameters(used = true)
    @SerializedName("rarity")
    public String rarity;
    @SerializedName("type")

    public String type;

    @SerializedName("basicTooltips")
    public ArrayList<NameType> basicTooltips;
    @SerializedName("advanceTooltips")
    public ArrayList<NameType> advanceTooltips;

    public ItemType basicTooltip(NameType basicTooltip) {
        if (this.basicTooltips == null) basicTooltips = new ArrayList<>();
        basicTooltips.add(basicTooltip);
        return self();
    }

    public ItemType advanceTooltip(NameType advanceTooltip) {
        if (this.advanceTooltips == null) advanceTooltips = new ArrayList<>();
        advanceTooltips.add(advanceTooltip);
        return self();
    }

    public ItemType type(String type) {
        this.type = type;
        return self();
    }

    public ItemType rarity(String rarity, boolean isUsed) {
        return advancementParameters("rarity", rarity, isUsed);
    }

    public ItemType OredictList(String oredictList) {
        if (OredictList == null) OredictList = new ArrayList<>();
        OredictList.add(oredictList);
        return self();
    }

    public ItemType maxStackSize(Integer maxStackSize) {
        this.maxStackSize = maxStackSize;
        return self();
    }

    public ItemType maxDurability(Integer maxDurability) {
        this.maxDurability = maxDurability;
        return self();
    }

    public ItemType icon(IconType icon) {
        this.icon = icon;
        return self();
    }

    public ItemType asBlock(BlockType asBlock) {
        this.asBlock = asBlock;
        return self();
    }

    public ItemType asFluid(String asFluid) {
        this.asFluid = asFluid;
        return self();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemType itemType = (ItemType) o;
        return maxStackSize == itemType.maxStackSize && maxDurability == itemType.maxDurability && fuelTime == itemType.fuelTime && Objects.equals(OredictList, itemType.OredictList) && Objects.equals(icon, itemType.icon) && Objects.equals(asBlock, itemType.asBlock) && Objects.equals(asFluid, itemType.asFluid) && Objects.equals(maxUseTime, itemType.maxUseTime) && Objects.equals(asFood, itemType.asFood) && Objects.equals(tool, itemType.tool) && Objects.equals(armor, itemType.armor) && Objects.equals(nbt, itemType.nbt) && Objects.equals(asEgg, itemType.asEgg) && Objects.equals(rarity, itemType.rarity) && Objects.equals(type, itemType.type) && Objects.equals(basicTooltips, itemType.basicTooltips) && Objects.equals(advanceTooltips, itemType.advanceTooltips);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxStackSize, maxDurability, OredictList, icon, asBlock, asFluid, maxUseTime, asFood, tool, armor, fuelTime, nbt, asEgg, rarity, type, basicTooltips, advanceTooltips);
    }
}
