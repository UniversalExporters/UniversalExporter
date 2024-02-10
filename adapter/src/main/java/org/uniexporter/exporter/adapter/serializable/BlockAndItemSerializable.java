package org.uniexporter.exporter.adapter.serializable;

import org.uniexporter.exporter.adapter.serializable.type.Type;

public class BlockAndItemSerializable {
    public String name;
    public String englishName;
    public String registerName;

    public Type type;

    public BlockAndItemSerializable name(String name) {
        this.name = name;
        return this;
    }

    public BlockAndItemSerializable englishName(String englishName) {
        this.englishName = englishName;
        return this;
    }

    public BlockAndItemSerializable registerName(String registerName) {
        this.registerName = registerName;
        return this;
    }

    public BlockAndItemSerializable type(Type type) {
        this.type = type;
        return this;
    }
}
