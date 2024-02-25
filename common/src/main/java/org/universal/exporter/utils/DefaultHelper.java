package org.universal.exporter.utils;

import net.minecraft.item.ItemStack;
import org.uniexporter.exporter.adapter.serializable.type.NameType;
import org.uniexporter.exporter.adapter.utils.IDefaultHelper;


public abstract class DefaultHelper<T extends DefaultHelper<T>> implements IDefaultHelper<T, ItemStack> {

    protected final String registerName;
    protected final boolean this$advanceParameters;

    public DefaultHelper(String registerName, boolean this$advanceParameters) {
        this.registerName = registerName;
        this.this$advanceParameters = this$advanceParameters;

    }
}
