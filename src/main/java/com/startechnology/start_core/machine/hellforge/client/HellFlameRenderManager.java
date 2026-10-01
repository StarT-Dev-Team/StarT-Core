package com.startechnology.start_core.machine.hellforge.client;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.startechnology.start_core.StarTConfig;
import com.startechnology.start_core.client.megastructure.InstanceCollector;
import com.startechnology.start_core.client.megastructure.MegastructureBlend;
import com.startechnology.start_core.client.megastructure.MegastructureMeshes;
import com.startechnology.start_core.client.megastructure.RenderTier;
import com.startechnology.start_core.client.megastructure.SceneCapture;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.ArrayList;
import java.util.List;

public final class HellFlameRenderManager {

    private static final double FULL_DISTANCE = 128;
    private static final InstanceCollector<HellFlameClientState> INSTANCES = new InstanceCollector<>(
            HellFlameClientState::center, () -> StarTConfig.INSTANCE.client.hellFlameMaxInstances);
    private static final List<HellFlameClientState> VISIBLE = new ArrayList<>();
    private static final List<HellFlameClientState> FALLBACK = new ArrayList<>();
    private static final int[] DETAIL_STEPS = { 12, 22, 32 };
    private static final float AMPLITUDE = 0.45f, FREQUENCY = 1.15f, RISE = 1.6f, SHARPNESS = 1;
    private static final Matrix4f INVERSE_PROJECTION = new Matrix4f();
    private static final Matrix4f VIEW_PROJECTION = new Matrix4f();
    private static final Matrix4f BOX = new Matrix4f();
    private static final Vector4f CORNER = new Vector4f();
    private static float[] drawn = new float[64];

    private HellFlameRenderManager() {}

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, HellFlameRenderManager::onRenderLevelStage);
    }

    public static RenderTier tier() {
        return RenderTier.resolve(StarTConfig.INSTANCE.client.hellFlameRenderMode, HellFlameShaders.loaded());
    }

    static void queue(HellFlameClientState state) {
        INSTANCES.add(state);
    }

    private static void onRenderLevelStage(RenderLevelStageEvent event) {
        var stage = event.getStage();
        if (stage == RenderLevelStageEvent.Stage.AFTER_SKY) {
            INSTANCES.clear();
            return;
        }
        if (stage != RenderLevelStageEvent.Stage.AFTER_PARTICLES || INSTANCES.isEmpty() ||
                !HellFlameShaders.loaded()) {
            return;
        }
        var camera = event.getCamera().getPosition();
        var frustum = event.getFrustum();
        VISIBLE.clear();
        FALLBACK.clear();
        for (var state : INSTANCES.closest(camera)) {
            if (!state.center().closerThan(camera, FULL_DISTANCE)) FALLBACK.add(state);
            else if (frustum.isVisible(state.bounds())) VISIBLE.add(state);
        }
        FALLBACK.addAll(INSTANCES.overflow());
        if (!VISIBLE.isEmpty()) draw(event, camera);
        if (FALLBACK.isEmpty()) return;
        var buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        for (var state : FALLBACK) HellFlameFallbackRenderer.render(state, event.getPoseStack(), buffers, camera);
        buffers.endBatch();
    }

    private static void draw(RenderLevelStageEvent event, Vec3 camera) {
        var shader = HellFlameShaders.flame;
        var modelView = event.getPoseStack().last().pose();
        var projection = event.getProjectionMatrix();
        VIEW_PROJECTION.set(projection).mul(modelView);
        shader.safeGetUniform("InvProjMat").set(INVERSE_PROJECTION.set(projection).invert());
        shader.safeGetUniform("Steps").set(DETAIL_STEPS[StarTConfig.INSTANCE.client.hellFlameDetail.ordinal()]);
        if (drawn.length < 4 * VISIBLE.size()) drawn = new float[4 * VISIBLE.size()];

        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GlConst.GL_ALWAYS);
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        MegastructureBlend.premultiplied();
        for (int i = VISIBLE.size() - 1, count = 0; i >= 0; i--, count++) {
            var state = VISIBLE.get(i);
            screenRect(state.bounds(), camera, count);
            if (overlapsDrawn(count)) SceneCapture.invalidate();
            shader.setSampler("SceneDepth", SceneCapture.capture().getDepthTextureId());
            setInstance(shader, state, camera);
            var cube = MegastructureMeshes.cube();
            cube.bind();
            cube.drawWithShader(modelView, projection, shader);
        }
        VertexBuffer.unbind();
        SceneCapture.invalidate();
        RenderSystem.depthFunc(GlConst.GL_LEQUAL);
        MegastructureBlend.restore();
        if (Minecraft.useShaderTransparency()) {
            var particles = Minecraft.getInstance().levelRenderer.getParticlesTarget();
            if (particles != null) particles.bindWrite(false);
        }
    }

    private static void setInstance(ShaderInstance shader, HellFlameClientState state, Vec3 camera) {
        var profile = state.profile();
        var frame = state.frame;
        var base = state.base();
        float x = (float) (base.x - camera.x), y = (float) (base.y - camera.y), z = (float) (base.z - camera.z);
        shader.safeGetUniform("BoxMat").set(profile.boxMatrix(x, y, z, state.basis(), BOX));
        shader.safeGetUniform("CenterOffset").set(x, y, z);
        shader.safeGetUniform("Basis").set(state.basis());
        shader.safeGetUniform("Bounds").set(profile.boundRadius, profile.boundHeight);
        shader.safeGetUniform("Shape").set(profile.baseRadius * frame.width, profile.radius * frame.width,
                profile.belly, frame.height);
        shader.safeGetUniform("Lift").set(frame.lift);
        shader.safeGetUniform("Turbulence").set(AMPLITUDE * frame.turbulence, FREQUENCY, RISE, SHARPNESS);
        shader.safeGetUniform("FlameTime").set(state.flameTime);
        shader.safeGetUniform("Core").set(frame.core);
        shader.safeGetUniform("Intensity").set(frame.intensity);
        shader.safeGetUniform("Theme").set(profile.theme.x(), profile.theme.y(), profile.theme.z());
        shader.safeGetUniform("Fringe").set(profile.fringe.x(), profile.fringe.y(), profile.fringe.z());
        shader.safeGetUniform("Smoke").set(profile.smoke);
        shader.safeGetUniform("Tint").set(frame.tintRed, frame.tintGreen, frame.tintBlue, frame.tintAmount);
        shader.safeGetUniform("Seed").set(state.seed());
    }

    private static void screenRect(AABB bounds, Vec3 camera, int index) {
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        for (int corner = 0; corner < 8; corner++) {
            double x = (corner & 1) == 0 ? bounds.minX : bounds.maxX;
            double y = (corner & 2) == 0 ? bounds.minY : bounds.maxY;
            double z = (corner & 4) == 0 ? bounds.minZ : bounds.maxZ;
            VIEW_PROJECTION.transform(CORNER.set((float) (x - camera.x), (float) (y - camera.y),
                    (float) (z - camera.z), 1));
            if (CORNER.w <= 0.05f) {
                minX = minY = -1;
                maxX = maxY = 1;
                break;
            }
            minX = Math.min(minX, CORNER.x / CORNER.w);
            minY = Math.min(minY, CORNER.y / CORNER.w);
            maxX = Math.max(maxX, CORNER.x / CORNER.w);
            maxY = Math.max(maxY, CORNER.y / CORNER.w);
        }
        drawn[4 * index] = minX;
        drawn[4 * index + 1] = minY;
        drawn[4 * index + 2] = maxX;
        drawn[4 * index + 3] = maxY;
    }

    private static boolean overlapsDrawn(int index) {
        int self = 4 * index;
        for (int other = 0; other < self; other += 4) {
            if (drawn[other] < drawn[self + 2] && drawn[self] < drawn[other + 2] &&
                    drawn[other + 1] < drawn[self + 3] && drawn[self + 1] < drawn[other + 3]) {
                return true;
            }
        }
        return false;
    }
}
