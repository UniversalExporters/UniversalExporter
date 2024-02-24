package org.universal.exporter.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.item.ItemStack;
import org.uniexporter.exporter.adapter.serializable.type.IconType;
import org.universal.exporter.UniExporter;

import java.io.IOException;
import java.util.Base64;

public class IconHelper extends IconType {

    public IconHelper(ItemStack stack) {
        try (NativeImage nativeImage = fromFrame(new FrameHelper(32, stack).fbo)) {
            smallIcon = Base64.getEncoder().encodeToString(nativeImage.getBytes());
        } catch (IOException e) {
            UniExporter.LOGGER.error(e.getMessage());
        }

        try (NativeImage nativeImage = fromFrame(new FrameHelper(128, stack).fbo)) {
            largeIcon = Base64.getEncoder().encodeToString(nativeImage.getBytes());
        } catch (IOException e) {
            UniExporter.LOGGER.error(e.getMessage());
        }

    }


    public static NativeImage fromFrame(Framebuffer fbo) {
        NativeImage img = new NativeImage(fbo.textureWidth, fbo.textureHeight, false);
        RenderSystem.bindTexture(fbo.getColorAttachment());
        img.loadFromTextureImage(0, false);
        img.mirrorVertically();
        return img;
    }

}
