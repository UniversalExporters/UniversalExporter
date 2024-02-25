package org.universal.exporter.mixin.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.universal.exporter.accessor.ToolMaterialSupplier;

import java.util.function.Supplier;

@Debug(export = true)
@Mixin(MiningToolItem.class)
public class MixinMiningToolItem implements ToolMaterialSupplier {
    @Unique
    private ToolMaterial universalExporter$material;
    @Inject(method = "<init>", at= @At("RETURN"))
    private void init(float attackDamage,
                      float attackSpeed,
                      ToolMaterial material,
                      TagKey<Block> effectiveBlocks,
                      Item.Settings settings,
                      CallbackInfo ci) {
        universalExporter$material = material;
    }

    @Override
    public ToolMaterial universalExporter$get() {
        return universalExporter$material;
    }
}
