package org.universal.exporter.mixin.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.universal.exporter.accessor.ItemStackAccessor;

@Mixin(ItemStack.class)
public abstract class MixinItemStack implements ItemStackAccessor {
    @Shadow public abstract Item getItem();

}
