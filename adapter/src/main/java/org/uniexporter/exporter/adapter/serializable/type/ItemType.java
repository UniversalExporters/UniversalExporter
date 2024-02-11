package org.uniexporter.exporter.adapter.serializable.type;

import org.uniexporter.exporter.adapter.faces.Self;

import java.util.ArrayList;

public class ItemType implements Self<ItemType> {
    public Integer maxStackSize;
    public Integer maxDurability;
    public ArrayList<String> OredictList;
    public IconType icon;
    public BlockType asBlock;
    public String asFluid;

    public String type;

    public ItemType type(String type) {
        this.type = type;
        return this;
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
}
