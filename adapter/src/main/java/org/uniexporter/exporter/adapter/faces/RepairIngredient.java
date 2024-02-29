package org.uniexporter.exporter.adapter.faces;

import org.jetbrains.annotations.Nullable;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ArmorType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.NbtType;

import java.util.concurrent.ConcurrentHashMap;

public interface RepairIngredient<T> {
    T repairIngredient(String registerName, @Nullable NbtType nbt);
}
