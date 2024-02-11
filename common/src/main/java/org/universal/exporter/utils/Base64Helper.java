package org.universal.exporter.utils;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.uniexporter.exporter.adapter.serializable.type.IconType;
import org.uniexporter.exporter.adapter.serializable.type.ItemType;
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

        try {
            Pair<String, String> pair = itemStackToBase64(stack);
            serializable.icon(new IconType()
                    .smallIcon(pair.getLeft())
                    .largeIcon(pair.getRight()));
        } catch (IOException e) {
            UniExporter.LOGGER.error("don't find {}", stack.getItem().getName());
        }

    }

    public static Pair<String, String> itemStackToBase64(ItemStack stack) throws IOException {
        Pair<FrameHelper, FrameHelper> pair = FrameHelper.of(stack);
        String smallBase64, largeBase64;
        try(NativeImage nativeImage = pair.getLeft().dumpFrom()) {
            smallBase64 = Base64.getEncoder().encodeToString(nativeImage.getBytes());
        }
        try(NativeImage nativeImage = pair.getRight().dumpFrom()) {
            largeBase64 = Base64.getEncoder().encodeToString(nativeImage.getBytes());
        }

        return new Pair<>(smallBase64, largeBase64);
    }

}
