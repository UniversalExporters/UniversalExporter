package org.universal.exporter.utils;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.uniexporter.exporter.adapter.serializable.BlockAndItemSerializable;
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

        var pair = FrameHelper.of(stack);
        try (NativeImage smallImg = pair.getLeft().dumpFrom()) {
            serializable.smallIcon(Base64.getEncoder().encodeToString(smallImg.getBytes()));

        } catch (IOException e) {
            UniExporter.LOGGER.error("don't find {}", stack.getItem().getName());
        }
        try (NativeImage largeImg = pair.getRight().dumpFrom()) {
            serializable.largeIcon(Base64.getEncoder().encodeToString(largeImg.getBytes()));
        } catch (IOException e) {
            UniExporter.LOGGER.error("don't find {}", stack.getItem().getName());
        }

    }

}
