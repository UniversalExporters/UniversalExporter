package org.uniexporter.exporter.adapter.serializable.type.itemAndBlock;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class NbtType {
    @SerializedName("entries")
    public ConcurrentHashMap<String, Object> entries;

    public static NbtType nbtType(Consumer<NbtType> consumer) {
        NbtType nbtType = new NbtType();
        nbtType.entries = new ConcurrentHashMap<>();
        consumer.accept(nbtType);
        return nbtType;
    }

    public void entry(String key, Object entry) {
        if (entries == null) entries = new ConcurrentHashMap<>();
        entries.put(key, entry);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NbtType nbtType = (NbtType) o;
        return Objects.equals(entries, nbtType.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entries);
    }
}
