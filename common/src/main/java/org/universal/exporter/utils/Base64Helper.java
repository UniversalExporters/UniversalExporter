package org.universal.exporter.utils;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
import org.uniexporter.exporter.adapter.serializable.type.IconType;
import org.uniexporter.exporter.adapter.serializable.type.itemAndBlock.ItemType;
import org.uniexporter.exporter.adapter.utils.IBase64Helper;
import org.universal.exporter.UniExporter;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.Base64;

public class Base64Helper implements Serializable, IBase64Helper<Item, ItemStack, Pair<String, String>> {
    @Serial
    private static final long serialVersionUID = -1242605576574999681L;
    private final IconType type;

    public Base64Helper(IconType type) {
        this.type = type;
    }

    public static Base64Helper icon() {
        return new Base64Helper(new IconType());
    }

    @Override
    public void itemToBase(Item item) {
        itemStackToBase(item.getDefaultStack());
    }
    @Override
    public IconType itemStackToBase(ItemStack stack) {
        Pair<String, String> pair = itemStackToBase64(stack);
        type.smallIcon = pair.getLeft();
        type.largeIcon = pair.getRight();
        return type;
    }

    public Pair<String, String> itemStackToBase64(ItemStack stack) {
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
