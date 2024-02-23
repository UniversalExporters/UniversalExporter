package org.uniexporter.exporter.adapter.utils;


import org.uniexporter.exporter.adapter.serializable.type.IconType;

public interface IBase64Helper<I, S, P> {
    void itemToBase(I item);
    IconType itemStackToBase(S stack);
    P itemStackToBase64(S stack);
}
