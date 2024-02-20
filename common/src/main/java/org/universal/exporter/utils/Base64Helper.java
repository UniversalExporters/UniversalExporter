package org.universal.exporter.utils;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.uniexporter.exporter.adapter.serializable.type.IconType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ItemType;
import org.universal.exporter.UniExporter;

import java.io.IOException;
import java.util.Base64;

public class Base64Helper {
    private final ItemType serializable;

    public Base64Helper(ItemType serializable) {
        this.serializable = serializable;
    }

    public void itemToBase(Item item) {
        itemStackToBase(item.getDefaultStack());
    }

    public void itemStackToBase(ItemStack stack) {
        Pair<String, String> pair = itemStackToBase64(stack);
        serializable.icon(new IconType()
                .smallIcon(pair.getLeft())
                .largeIcon(pair.getRight()));
    }

    public static Pair<String, String> itemStackToBase64(ItemStack stack) {
        Pair<FrameHelper, FrameHelper> pair = FrameHelper.of(stack);
        String smallBase64, largeBase64;
        try(
                NativeImage nativeImage = pair.getLeft().dumpFrom();
                NativeImage nativeImage1 = pair.getRight().dumpFrom();
        ) {
            smallBase64 = Base64.getEncoder().encodeToString(nativeImage.getBytes());
            largeBase64 = Base64.getEncoder().encodeToString(nativeImage1.getBytes());
            return new Pair<>(smallBase64, largeBase64);
        } catch (IOException e) {
            UniExporter.LOGGER.error("don't find {}", stack.getItem().getName());
        }
        return new Pair<>(null, null);
    }

}
