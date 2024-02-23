package org.uniexporter.exporter.adapter.utils;

import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.uniexporter.exporter.adapter.serializable.type.NameType;

public interface IDefaultHelper<T, S> {
    T language(NameType serializable, S stack);
    T self();

    T language(NameType blockAndItem, String translationKey);
}
