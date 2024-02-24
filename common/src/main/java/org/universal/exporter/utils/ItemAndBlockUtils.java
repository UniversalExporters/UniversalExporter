package org.universal.exporter.utils;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.apache.commons.codec.binary.Base64;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.uniexporter.exporter.adapter.serializable.type.IconType;

import java.io.IOException;

import static org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ItemType.itemType;

public class ItemAndBlockUtils {
    public static void defaultItemProperties(ItemStack stack, BlockAndItemSerializable blockAndItem) {
        blockAndItem.type = itemType(itemType -> {
            itemType.maxStackSize = stack.getItem().getMaxCount();
            itemType.maxDurability = stack.getItem().getMaxDamage();
            stack.streamTags().forEach(itemTagKey -> {
                itemType.OredictList(itemTagKey.id().toString());
            });
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
