package org.universal.exporter.accessor;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public interface IdentifierAccessor extends ItemConvertible {
    Identifier universalExporter$self();
    default ItemStack asStack() {
        return asItem().getDefaultStack();
    }

    @Override
    default Item asItem() {
        return Registries.ITEM.get(universalExporter$self());
    }
}
