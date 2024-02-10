package org.uniexporter.exporter.adapter.serializable.type;

import java.util.ArrayList;
import java.util.Base64;

public class ItemType extends Type {
    public Integer maxStackSize;
    public Integer maxDurability;
    public ArrayList<String> OredictList;
    public Base64 smallIcon;
    public Base64 largeIcon;

    public void OredictList(ArrayList<String> oredictList) {
        OredictList = oredictList;
    }

    public void maxStackSize(Integer maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    public void maxDurability(Integer maxDurability) {
        this.maxDurability = maxDurability;
    }

    public ItemType largeIcon(Base64 largeIcon) {
        this.largeIcon = largeIcon;
        return this;
    }

    public ItemType smallIcon(Base64 smallIcon) {
        this.smallIcon = smallIcon;
        return this;
    }
}
