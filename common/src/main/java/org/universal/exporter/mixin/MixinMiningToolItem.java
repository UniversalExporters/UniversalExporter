package org.universal.exporter.mixin;

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

import java.util.function.Supplier;

@Debug(export = true)
@Mixin(MiningToolItem.class)
public class MixinMiningToolItem implements Supplier<ToolMaterial> {
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
    public ToolMaterial get() {
        return universalExporter$material;
    }
}