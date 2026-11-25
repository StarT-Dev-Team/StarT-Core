package com.startechnology.start_core.client.megastructure;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;

public final class SceneCapture {

    private static RenderTarget copy;
    private static boolean captured;

    private SceneCapture() {}

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(SceneCapture::onRenderLevelStage);
    }

    private static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) captured = false;
    }

    public static RenderTarget capture() {
        if (captured) return copy;
        captured = true;

        var main = Minecraft.getInstance().getMainRenderTarget();
        if (copy == null) {
            copy = new TextureTarget(main.width, main.height, true, Minecraft.ON_OSX);
            copy.setClearColor(0, 0, 0, 0);
        }
        if (main.isStencilEnabled() && !copy.isStencilEnabled()) copy.enableStencil();
        if (main.width != copy.width || main.height != copy.height) {
            copy.resize(main.width, main.height, Minecraft.ON_OSX);
        }

        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, main.frameBufferId);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, copy.frameBufferId);
        GlStateManager._glBlitFrameBuffer(0, 0, main.width, main.height, 0, 0, copy.width, copy.height,
                GlConst.GL_COLOR_BUFFER_BIT | GlConst.GL_DEPTH_BUFFER_BIT, GlConst.GL_NEAREST);
        main.bindWrite(false);
        return copy;
    }

    public static void invalidate() {
        captured = false;
    }
}
