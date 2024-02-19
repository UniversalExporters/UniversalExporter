package org.uniexporter.exporter.adapter.serializable.type.itemAndBlock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class NbtType {
    public ConcurrentHashMap<String, Object> entries;

    public static NbtType nbtType(Consumer<NbtType> consumer) {
        NbtType nbtType = new NbtType();
        nbtType.entries = new ConcurrentHashMap<>();
        consumer.accept(nbtType);
        return nbtType;
    }
}
