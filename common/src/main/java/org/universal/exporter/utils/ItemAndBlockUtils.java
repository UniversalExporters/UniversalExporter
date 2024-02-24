package org.universal.exporter.utils;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;

import static org.uniexporter.exporter.adapter.serializable.type.IconType.iconType;
import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ItemType.itemType;

public class ItemAndBlockUtils {
    public static void defaultItemProperties(ItemStack stack, BlockAndItemSerializable blockAndItem, FrameHelper smallIconFrame, FrameHelper largeIconFrame) {
        blockAndItem.type = itemType(itemType -> {
            itemType.maxStackSize = stack.getItem().getMaxCount();
            itemType.maxDurability = stack.getItem().getMaxDamage();
            stack.streamTags().forEach(itemTagKey -> {
                itemType.OredictList(itemTagKey.id().toString());
            });

            itemType.icon(iconType(iconType -> {

                iconType.smallIcon = smallIconFrame.base64();
                iconType.largeIcon = largeIconFrame.base64();
            }));
//            IconType icon = new IconType();
//            FrameHelper left = new FrameHelper(32, stack);
//            FrameHelper right = new FrameHelper(128, stack);
//            try(NativeImage smallIcon = left.dumpFrom();) {
//                icon.smallIcon(left.base64(smallIcon));
//            }
//            try(var largeIcon = right.dumpFrom()) {
//                icon.largeIcon(right.base64(largeIcon));
//            }
        });
    }
}
