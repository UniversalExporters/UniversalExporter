package org.universal.exporter.utils;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.uniexporter.exporter.adapter.serializable.BlockAndItems;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ItemType;

import java.util.function.Function;

import static org.universal.exporter.utils.LanguageHelper.en_us;
import static org.universal.exporter.utils.LanguageHelper.zh_cn;

public class ItemAndBlockHelper {
    private final BlockAndItemSerializable serializable;
    private final Base64Helper base64Helper;

    public ItemAndBlockHelper(BlockAndItemSerializable serializable) {
        this.serializable = serializable;
        this.base64Helper = new Base64Helper(serializable.type);
    }

    public ItemAndBlockHelper init(Item item) {
        base64Helper.itemToBase(item);
        serializable
                .englishName(en_us().get(item.getTranslationKey()))
                .name(zh_cn().get(item.getTranslationKey()));
        return this;
    }

    public <T> ItemAndBlockHelper setup(Function<ItemType, ItemAndBlockHelper> function) {
        return function.apply(serializable.type);
    }

    public ItemAndBlockHelper save(BlockAndItems blockAndItems, Identifier registryId) {
        blockAndItems.items.put(registryId.toString(), serializable);
        return this;
    }


}
