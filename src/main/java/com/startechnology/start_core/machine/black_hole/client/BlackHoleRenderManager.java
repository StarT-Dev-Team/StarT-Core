package com.startechnology.start_core.machine.black_hole.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.startechnology.start_core.StarTConfig;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.client.megastructure.InstanceCollector;
import com.startechnology.start_core.client.megastructure.RenderTier;
import com.startechnology.start_core.client.megastructure.SceneCapture;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public final class BlackHoleRenderManager {

    public record Lens(BlackHoleAnimation.Frame frame, Vec3 center, Vector3f[] basis, float proxyRadius,
                       float seconds) {}

    private static final InstanceCollector<Lens> LENSES = new InstanceCollector<>(Lens::center,
            () -> StarTConfig.INSTANCE.client.blackHoleMaxInstances);
    private static BlackHoleShaderInstance shader;

    private BlackHoleRenderManager() {}

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(BlackHoleRenderManager::onRenderLevelStage);
    }

    @Mod.EventBusSubscriber(modid = StarTCore.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {

        @SubscribeEvent
        public static void onRegisterShaders(RegisterShadersEvent event) {
            try {
                event.registerShader(new BlackHoleShaderInstance(event.getResourceProvider(),
                        StarTCore.resourceLocation("black_hole_lensing"), DefaultVertexFormat.POSITION),
                        instance -> shader = (BlackHoleShaderInstance) instance);
            } catch (Exception e) {
                shader = null;
                StarTCore.LOGGER.error("Failed to load the black hole lensing shader", e);
            }
        }
    }

    public static RenderTier tier() {
        return RenderTier.resolve(StarTConfig.INSTANCE.client.blackHoleRenderMode,
                shader != null && !Minecraft.useShaderTransparency());
    }

    public static boolean claimInstance() {
        return LENSES.claim();
    }

    public static void queue(Lens lens) {
        LENSES.add(lens);
    }

    private static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            LENSES.clear();
            return;
        }
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES || LENSES.isEmpty() || shader == null) {
            return;
        }

        var camera = event.getCamera().getPosition();
        var main = Minecraft.getInstance().getMainRenderTarget();
        var copy = SceneCapture.capture();

        var projection = event.getProjectionMatrix();
        shader.setSampler("SceneColor", copy.getColorTextureId());
        shader.setSampler("SceneDepth", copy.getDepthTextureId());
        if (shader.MODEL_VIEW_MATRIX != null) shader.MODEL_VIEW_MATRIX.set(event.getPoseStack().last().pose());
        if (shader.PROJECTION_MATRIX != null) shader.PROJECTION_MATRIX.set(projection);
        if (shader.SCREEN_SIZE != null) shader.SCREEN_SIZE.set((float) main.width, (float) main.height);
        shader.inverseProjection.set(new Matrix4f(projection).invert());
        var fog = RenderSystem.getShaderFogColor();
        shader.fallbackColor.set(fog[0], fog[1], fog[2]);
        shader.steps.set(StarTConfig.INSTANCE.client.blackHoleLensingSteps);
        shader.bendStrength.set((float) StarTConfig.INSTANCE.client.blackHoleLensingStrength);

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        for (var lens : LENSES.closest(camera)) {
            apply(lens, camera);
            shader.apply();
            drawFullScreenQuad();
        }
        shader.clear();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    private static void apply(Lens lens, Vec3 camera) {
        var frame = lens.frame();
        shader.cameraOffset.set((float) (camera.x - lens.center().x), (float) (camera.y - lens.center().y),
                (float) (camera.z - lens.center().z));
        shader.rs.set(frame.horizon / 2.598f);
        shader.diskInner.set(frame.diskInner);
        shader.diskOuter.set(frame.diskOuter);
        shader.diskTangent.set(lens.basis()[0]);
        shader.diskNormal.set(lens.basis()[2]);
        shader.theme.set(frame.red, frame.green, frame.blue);
        shader.spin.set(frame.spin);
        shader.time.set(lens.seconds());
        shader.diskIntensity.set(frame.diskIntensity);
        shader.diskSpeed.set(frame.diskSpeed);
        shader.diskHeat.set(frame.diskHeat);
        shader.ringIntensity.set(frame.ringIntensity);
        shader.glowIntensity.set(frame.glowIntensity);
        shader.flare.set(frame.flare);
        shader.flareSize.set(frame.flareSize);
        shader.shockRadius.set(frame.shockRadius);
        shader.shockIntensity.set(frame.shockIntensity);
        shader.proxyRadius.set(lens.proxyRadius());
    }

    private static void drawFullScreenQuad() {
        var builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        builder.vertex(-1, -1, 0).endVertex();
        builder.vertex(1, -1, 0).endVertex();
        builder.vertex(1, 1, 0).endVertex();
        builder.vertex(-1, 1, 0).endVertex();
        BufferUploader.draw(builder.end());
    }
}
