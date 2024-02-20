package org.universal.exporter.utils;

import net.minecraft.item.ItemStack;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;

public abstract class DefaultHelper<T extends DefaultHelper<T>> {
    public T language(BlockAndItemSerializable blockAndItem, ItemStack item) {
        LanguageHelper.get(blockAndItem, item.getTranslationKey());
        return self();
    }

    public abstract T self();
}
