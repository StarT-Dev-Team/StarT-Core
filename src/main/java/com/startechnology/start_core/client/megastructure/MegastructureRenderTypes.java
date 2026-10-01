package com.startechnology.start_core.client.megastructure;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class MegastructureRenderTypes extends RenderType {

    private static final Function<ResourceLocation, RenderType> ADDITIVE = Util.memoize(
            texture -> create("start_core_megastructure_additive", DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.QUADS, 65536, false, false,
                    CompositeState.builder()
                            .setShaderState(RENDERTYPE_EYES_SHADER)
                            .setTextureState(new TextureStateShard(texture, true, false))
                            .setTransparencyState(ADDITIVE_TRANSPARENCY)
                            .setCullState(NO_CULL)
                            .setWriteMaskState(COLOR_WRITE)
                            .createCompositeState(false)));

    private static final Function<ResourceLocation, RenderType> TRANSLUCENT = Util.memoize(
            texture -> create("start_core_megastructure_translucent", DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.QUADS, 65536, false, false,
                    CompositeState.builder()
                            .setShaderState(RENDERTYPE_EYES_SHADER)
                            .setTextureState(new TextureStateShard(texture, true, false))
                            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                            .setCullState(NO_CULL)
                            .setWriteMaskState(COLOR_WRITE)
                            .createCompositeState(false)));

    public static RenderType additive(ResourceLocation texture) {
        return ADDITIVE.apply(texture);
    }

    public static RenderType translucent(ResourceLocation texture) {
        return TRANSLUCENT.apply(texture);
    }

    private MegastructureRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                                     boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setup, clear);
    }
}
