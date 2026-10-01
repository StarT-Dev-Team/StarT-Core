package com.startechnology.start_core.machine.black_hole.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.startechnology.start_core.StarTCore;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class BlackHoleRenderTypes extends RenderType {

    public static final ResourceLocation BEAM_TEXTURE = StarTCore.resourceLocation("textures/misc/black_hole_beam.png");
    public static final ResourceLocation DISK_TEXTURE = StarTCore.resourceLocation("textures/misc/black_hole_disk.png");
    public static final ResourceLocation GLOW_TEXTURE = StarTCore.resourceLocation("textures/misc/black_hole_glow.png");
    public static final ResourceLocation BAND_TEXTURE = StarTCore.resourceLocation("textures/misc/black_hole_band.png");

    public static final RenderType HORIZON = create("start_core_black_hole_horizon",
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 8192, false, false,
            CompositeState.builder()
                    .setShaderState(POSITION_COLOR_SHADER)
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .createCompositeState(false));

    private BlackHoleRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                                 boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setup, clear);
    }
}
