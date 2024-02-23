package org.uniexporter.exporter.adapter.utils;

import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;

public interface IDefaultHelper<T, S> {
    T language(BlockAndItemSerializable serializable, S stack);
    T self();
}
