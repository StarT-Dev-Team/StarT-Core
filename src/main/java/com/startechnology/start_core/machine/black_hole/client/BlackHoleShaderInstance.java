package com.startechnology.start_core.machine.black_hole.client;

import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlackHoleShaderInstance extends ShaderInstance {

    public final AbstractUniform inverseProjection;
    public final AbstractUniform cameraOffset;
    public final AbstractUniform fallbackColor;
    public final AbstractUniform rs;
    public final AbstractUniform diskInner;
    public final AbstractUniform diskOuter;
    public final AbstractUniform diskNormal;
    public final AbstractUniform diskTangent;
    public final AbstractUniform theme;
    public final AbstractUniform spin;
    public final AbstractUniform time;
    public final AbstractUniform diskIntensity;
    public final AbstractUniform diskSpeed;
    public final AbstractUniform diskHeat;
    public final AbstractUniform ringIntensity;
    public final AbstractUniform glowIntensity;
    public final AbstractUniform flare;
    public final AbstractUniform flareSize;
    public final AbstractUniform shockRadius;
    public final AbstractUniform shockIntensity;
    public final AbstractUniform proxyRadius;
    public final AbstractUniform steps;
    public final AbstractUniform bendStrength;

    public BlackHoleShaderInstance(ResourceProvider resourceProvider, ResourceLocation location,
                                   VertexFormat vertexFormat) throws IOException {
        super(resourceProvider, location, vertexFormat);
        inverseProjection = safeGetUniform("InvProjMat");
        cameraOffset = safeGetUniform("CameraOffset");
        fallbackColor = safeGetUniform("FallbackColor");
        rs = safeGetUniform("Rs");
        diskInner = safeGetUniform("DiskInner");
        diskOuter = safeGetUniform("DiskOuter");
        diskNormal = safeGetUniform("DiskNormal");
        diskTangent = safeGetUniform("DiskTangent");
        theme = safeGetUniform("Theme");
        spin = safeGetUniform("Spin");
        time = safeGetUniform("Time");
        diskIntensity = safeGetUniform("DiskIntensity");
        diskSpeed = safeGetUniform("DiskSpeed");
        diskHeat = safeGetUniform("DiskHeat");
        ringIntensity = safeGetUniform("RingIntensity");
        glowIntensity = safeGetUniform("GlowIntensity");
        flare = safeGetUniform("Flare");
        flareSize = safeGetUniform("FlareSize");
        shockRadius = safeGetUniform("ShockRadius");
        shockIntensity = safeGetUniform("ShockIntensity");
        proxyRadius = safeGetUniform("ProxyRadius");
        steps = safeGetUniform("Steps");
        bendStrength = safeGetUniform("BendStrength");
    }
}
