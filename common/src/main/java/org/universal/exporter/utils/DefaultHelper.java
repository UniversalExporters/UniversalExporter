package org.universal.exporter.utils;

import net.minecraft.item.ItemStack;
import org.uniexporter.exporter.adapter.serializable.type.NameType;
import org.uniexporter.exporter.adapter.utils.IDefaultHelper;

import static org.universal.exporter.accessor.LanguageAccessor.en_us;
import static org.universal.exporter.accessor.LanguageAccessor.zh_cn;


public abstract class DefaultHelper<T extends DefaultHelper<T>> implements IDefaultHelper<T, ItemStack> {

    protected final String registerName;
    protected final boolean this$advanceParameters;

    public DefaultHelper(String registerName, boolean this$advanceParameters) {
        this.registerName = registerName;
        this.this$advanceParameters = this$advanceParameters;

    }

    @Override
    public T language(NameType blockAndItem, ItemStack item) {
        en_us().get(blockAndItem, item.getTranslationKey());
        zh_cn().get(blockAndItem, item.getTranslationKey());
        return self();
    }

    @Override
    public T language(NameType blockAndItem, String translationKey) {
        en_us().get(blockAndItem, translationKey);
        zh_cn().get(blockAndItem, translationKey);
        return self();
    }
}
