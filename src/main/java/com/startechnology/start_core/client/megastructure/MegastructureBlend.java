package com.startechnology.start_core.client.megastructure;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.BlendMode;
import com.mojang.blaze3d.systems.RenderSystem;

public final class MegastructureBlend {

    private static final BlendMode OPAQUE = new BlendMode();

    private MegastructureBlend() {}

    public static void additive() {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
    }

    public static void premultiplied() {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    public static void restore() {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        OPAQUE.apply();
    }
}
