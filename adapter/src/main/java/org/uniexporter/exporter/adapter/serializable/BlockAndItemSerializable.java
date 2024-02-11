package org.uniexporter.exporter.adapter.serializable;

import org.uniexporter.exporter.adapter.serializable.type.ItemType;

public class BlockAndItemSerializable {
    public String name;
    public String englishName;

    public ItemType type;



    public BlockAndItemSerializable name(String name) {
        this.name = name;
        return this;
    }

    public BlockAndItemSerializable englishName(String englishName) {
        this.englishName = englishName;
        return this;
    }

    public BlockAndItemSerializable type(ItemType type) {
        this.type = type;
        return this;
    }
}
