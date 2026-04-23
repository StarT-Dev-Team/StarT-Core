package com.startechnology.start_core.machine.komaru.client.v2;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class KomaruShaderInstance extends ShaderInstance {

    public final AbstractUniform MODEL_VIEW_MATRIX;
    public final AbstractUniform PROJECTION_MATRIX;
    public final AbstractUniform SCREEN_SIZE;
    public final AbstractUniform GAME_TIME;
    public final AbstractUniform INVERSE_PROJECTION_MATRIX;
    public final AbstractUniform INVERSE_VIEW_ROTATION_MATRIX;
    public final AbstractUniform CAMERA_NEAR_PLANE;
    public final AbstractUniform CAMERA_FAR_PLANE;
    public final AbstractUniform CAMERA_POSITION;
    public final AbstractUniform KOMARU_POSITION;

    public RenderTarget renderTarget;

    public KomaruShaderInstance(ResourceProvider resourceProvider, ResourceLocation resourceLocation, VertexFormat vertexFormat) throws IOException {
        super(resourceProvider, resourceLocation, vertexFormat);

        MODEL_VIEW_MATRIX = safeGetUniform("ModelViewMat");
        PROJECTION_MATRIX = safeGetUniform("ProjMat");
        SCREEN_SIZE = safeGetUniform("ScreenSize");
        GAME_TIME = safeGetUniform("GameTime");
        INVERSE_PROJECTION_MATRIX = safeGetUniform("InvProjMat");
        INVERSE_VIEW_ROTATION_MATRIX = safeGetUniform("InvViewRotMat");
        CAMERA_NEAR_PLANE = safeGetUniform("CameraNearPlane");
        CAMERA_FAR_PLANE = safeGetUniform("CameraFarPlane");
        CAMERA_POSITION = safeGetUniform("CameraPosition");
        KOMARU_POSITION = safeGetUniform("KomaruPosition");
    }

    public void updateRenderTarget(RenderTarget mainRenderTarget) {
        if (renderTarget == null ) {
            renderTarget = new TextureTarget(mainRenderTarget.width, mainRenderTarget.height, true, Minecraft.ON_OSX);
            renderTarget.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        }

        if (mainRenderTarget.width != renderTarget.width || mainRenderTarget.height != renderTarget.height) {
            renderTarget.resize(mainRenderTarget.width, mainRenderTarget.height, Minecraft.ON_OSX);
        }
    }
}
