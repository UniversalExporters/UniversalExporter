package org.universal.exporter.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Pair;
import org.joml.Matrix4f;
import org.uniexporter.exporter.adapter.utils.IFrameHelper;

import java.io.IOException;
import java.util.Base64;

public class FrameHelper implements IFrameHelper<NativeImage, ItemStack, BakedModel, ItemRenderer> {
    public final Framebuffer fbo;
    private MatrixStack matrixStack;
    private static final int lightSet = 15728880;

    public FrameHelper(int size, ItemStack stack) {
        this.fbo = new SimpleFramebuffer(size, size, true, MinecraftClient.IS_SYSTEM_MAC);
        begin();
        // code
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        render(stack, 0, 0, itemRenderer.getModel(stack, null, null, 0), itemRenderer);
        end();
    }

    public static Pair<FrameHelper, FrameHelper> of(ItemStack stack) {
        return new Pair<>(new FrameHelper(32, stack), new FrameHelper(128, stack));
    }

    @Override
    public void begin() {
        this.matrixStack = RenderSystem.getModelViewStack();
        this.matrixStack.push();
        this.matrixStack.loadIdentity();
        RenderSystem.backupProjectionMatrix();
        Matrix4f projection = new Matrix4f().ortho(0, 16, 16, 0, -150, -150);
        RenderSystem.setProjectionMatrix(projection, VertexSorter.BY_Z);
        this.fbo.beginWrite(true);
        this.fbo.beginRead();
    }

    @Override
    public void end() {
        RenderSystem.restoreProjectionMatrix();
        this.matrixStack.pop();
        this.fbo.endWrite();
        this.fbo.endRead();
    }
    @Override
    public void render(ItemStack stack, int x, int y, BakedModel baked, ItemRenderer renderer) {
        RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y, 100.0F);
        matrixStack.translate(8.0D, 8.0D, 0.0D);
        matrixStack.scale(1.0F, -1.0F, 1.0F);
        matrixStack.scale(16.0F, 16.0F, 16.0F);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        boolean sideLit = baked.isSideLit();
        if (!sideLit) DiffuseLighting.disableGuiDepthLighting();

        renderer.renderItem(stack, ModelTransformationMode.GUI, false, matrixStack2, immediate, lightSet, OverlayTexture.DEFAULT_UV, baked);
        immediate.draw();
        RenderSystem.enableDepthTest();
        if (sideLit) DiffuseLighting.disableGuiDepthLighting();
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();

    }

    @Override
    public String base64(NativeImage image) {
        String encode;
        try {
            try {
                encode = Base64.getEncoder().encodeToString(image.getBytes());
            } catch (Throwable e) {
                if (image != null) {
                    try {
                        image.close();
                    } catch (Throwable e1) {
                        e.addSuppressed(e1);
                    }
                }
                throw e;
            }
            return encode;
        } catch (Throwable e) {
            return "";
        }
    }

    public String base64() {
        try(NativeImage nativeImage = dumpFrom()) {
            return Base64.getEncoder().encodeToString(nativeImage.getBytes());
        } catch (IOException ignored) {
        }
        return null;
    }

    @Override
    public NativeImage dumpFrom() {
        NativeImage img = new NativeImage(fbo.textureWidth, fbo.textureHeight, false);
        RenderSystem.bindTexture(fbo.getColorAttachment());
        img.loadFromTextureImage(0, false);
        img.mirrorVertically();
        return img;
    }
}
