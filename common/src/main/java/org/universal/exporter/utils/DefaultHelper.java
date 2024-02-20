package org.universal.exporter.utils;

import net.minecraft.item.ItemStack;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;

public abstract class DefaultHelper<T extends DefaultHelper<T>> {

    protected final String registerName;
    protected final boolean this$advanceParameters;
    public DefaultHelper(String registerName, boolean this$advanceParameters) {
        this.registerName = registerName;
        this.this$advanceParameters = this$advanceParameters;
    }

    public T language(BlockAndItemSerializable blockAndItem, ItemStack item) {
        LanguageHelper.get(blockAndItem, item.getTranslationKey());
        return self();
    }

    public abstract T self();
}
