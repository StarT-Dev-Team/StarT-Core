package com.startechnology.start_core.machine.dimensional_destabiliser.client;

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

import static com.startechnology.start_core.machine.dimensional_destabiliser.DestabiliserRift.*;

public final class RiftRenderManager {

    static final double FULL_DISTANCE = 96;
    private static final InstanceCollector<RiftClientState> INSTANCES = new InstanceCollector<>(
            RiftClientState::centre, () -> StarTConfig.INSTANCE.client.riftMaxInstances);
    private static final List<RiftClientState> VISIBLE = new ArrayList<>();
    private static final Matrix4f INVERSE_PROJECTION = new Matrix4f();
    private static final Matrix4f VIEW_PROJECTION = new Matrix4f();
    private static final Matrix4f BOX = new Matrix4f();
    private static final Vector4f CORNER = new Vector4f();
    private static float[] drawn = new float[64];

    private RiftRenderManager() {}

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, RiftRenderManager::onRenderLevelStage);
    }

    public static RenderTier tier() {
        return RenderTier.resolve(StarTConfig.INSTANCE.client.riftRenderMode,
                RiftShaders.loaded() && !Minecraft.useShaderTransparency());
    }

    static void queue(RiftClientState state) {
        INSTANCES.add(state);
    }

    private static void onRenderLevelStage(RenderLevelStageEvent event) {
        var stage = event.getStage();
        if (stage == RenderLevelStageEvent.Stage.AFTER_SKY) {
            INSTANCES.clear();
            return;
        }
        if (stage != RenderLevelStageEvent.Stage.AFTER_PARTICLES || INSTANCES.isEmpty() || !RiftShaders.loaded()) {
            return;
        }
        var camera = event.getCamera().getPosition();
        var frustum = event.getFrustum();
        VISIBLE.clear();
        for (var state : INSTANCES.closest(camera)) {
            if (frustum.isVisible(state.bounds())) VISIBLE.add(state);
        }
        if (!VISIBLE.isEmpty()) draw(event, camera);
    }

    private static void draw(RenderLevelStageEvent event, Vec3 camera) {
        var shader = RiftShaders.rift;
        var modelView = event.getPoseStack().last().pose();
        var projection = event.getProjectionMatrix();
        VIEW_PROJECTION.set(projection).mul(modelView);
        shader.safeGetUniform("InvProjMat").set(INVERSE_PROJECTION.set(projection).invert());
        shader.safeGetUniform("Detail").set(StarTConfig.INSTANCE.client.riftDetail.ordinal());
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
            var capture = SceneCapture.capture();
            shader.setSampler("SceneColor", capture.getColorTextureId());
            shader.setSampler("SceneDepth", capture.getDepthTextureId());
            setInstance(shader, state, camera);
            var cube = MegastructureMeshes.cube();
            cube.bind();
            cube.drawWithShader(modelView, projection, shader);
        }
        VertexBuffer.unbind();
        SceneCapture.invalidate();
        RenderSystem.depthFunc(GlConst.GL_LEQUAL);
        MegastructureBlend.restore();
    }

    private static void setInstance(ShaderInstance shader, RiftClientState state, Vec3 camera) {
        var frame = state.frame;
        var centre = state.centre();
        float x = (float) (centre.x - camera.x), y = (float) (centre.y - camera.y), z = (float) (centre.z - camera.z);
        shader.safeGetUniform("BoxMat").set(BOX.translation(x, y, z).scale(BOUND_RADIUS, BOUND_HALF_HEIGHT,
                BOUND_RADIUS));
        shader.safeGetUniform("CenterOffset").set(x, y, z);
        shader.safeGetUniform("Basis").set(state.basis(camera));
        shader.safeGetUniform("Bounds").set(BOUND_RADIUS, BOUND_HALF_HEIGHT);
        shader.safeGetUniform("Shape").set(HALF_HEIGHT, LENS_REACH);
        shader.safeGetUniform("Width").set(frame.width);
        shader.safeGetUniform("Glow").set(frame.glow);
        shader.safeGetUniform("Lens").set(frame.lens);
        shader.safeGetUniform("Turbulence").set(frame.turbulence);
        shader.safeGetUniform("Zip").set(frame.zipLow, frame.zipHigh);
        shader.safeGetUniform("Flash").set(frame.flash);
        shader.safeGetUniform("RiftTime").set(state.riftTime);
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
