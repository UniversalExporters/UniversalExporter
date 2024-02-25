package org.universal.exporter.accessor;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public interface ItemStackAccessor {
    default Identifier getId() {
        return Registries.ITEM.getId(getItem());
    }

    Item getItem();
}
