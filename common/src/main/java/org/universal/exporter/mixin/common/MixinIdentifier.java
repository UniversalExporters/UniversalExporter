package org.universal.exporter.mixin.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.universal.exporter.accessor.IdentifierAccessor;

@Mixin(Identifier.class)
public class MixinIdentifier implements IdentifierAccessor {

    @Override
    public Identifier universalExporter$self() {
        return (Identifier) (Object) this;
    }
}
