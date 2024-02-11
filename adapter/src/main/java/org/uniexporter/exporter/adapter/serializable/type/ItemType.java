package org.uniexporter.exporter.adapter.serializable.type;

import java.util.ArrayList;

public class ItemType {
    public Integer maxStackSize;
    public Integer maxDurability;
    public ArrayList<String> OredictList;
    public String smallIcon;
    public String largeIcon;
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
        return this;
    }

    public ItemType maxStackSize(Integer maxStackSize) {
        this.maxStackSize = maxStackSize;
        return this;
    }

    public ItemType maxDurability(Integer maxDurability) {
        this.maxDurability = maxDurability;
        return this;
    }

    public ItemType largeIcon(String largeIcon) {
        this.largeIcon = largeIcon;
        return this;
    }

    public ItemType smallIcon(String smallIcon) {
        this.smallIcon = smallIcon;
        return this;
    }

    public ItemType asBlock(BlockType asBlock) {
        this.asBlock = asBlock;
        return this;
    }

    public ItemType asFluid(String asFluid) {
        this.asFluid = asFluid;
        return this;
    }
}
