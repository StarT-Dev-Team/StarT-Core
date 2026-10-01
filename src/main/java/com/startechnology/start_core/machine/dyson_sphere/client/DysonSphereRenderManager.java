package com.startechnology.start_core.machine.dyson_sphere.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.startechnology.start_core.StarTConfig;
import com.startechnology.start_core.client.megastructure.InstanceCollector;
import com.startechnology.start_core.client.megastructure.MegastructureBlend;
import com.startechnology.start_core.client.megastructure.MegastructureMeshes;
import com.startechnology.start_core.client.megastructure.RenderTier;
import com.startechnology.start_core.client.megastructure.SceneCapture;
import com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.RING_COUNT;

public final class DysonSphereRenderManager {

    public record Instance(Vec3 center, DysonAnimation.Frame frame, Matrix3f[] rings, float seconds, float seed,
                           float ambient) {}

    private static final InstanceCollector<Instance> INSTANCES = new InstanceCollector<>(Instance::center,
            () -> StarTConfig.INSTANCE.client.dysonMaxInstances);
    private static final List<Instance> DRAWN = new ArrayList<>();
    private static final Matrix4f IDENTITY = new Matrix4f();

    private DysonSphereRenderManager() {}

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(DysonSphereRenderManager::onRenderLevelStage);
    }

    public static RenderTier tier() {
        return RenderTier.resolve(StarTConfig.INSTANCE.client.dysonRenderMode, DysonShaders.loaded());
    }

    public static boolean claimInstance() {
        return INSTANCES.claim();
    }

    public static void queue(Instance instance) {
        INSTANCES.add(instance);
    }

    private static void onRenderLevelStage(RenderLevelStageEvent event) {
        var stage = event.getStage();
        if (stage == RenderLevelStageEvent.Stage.AFTER_SKY) {
            INSTANCES.clear();
            DRAWN.clear();
            return;
        }
        if (!DysonShaders.loaded()) return;
        if (stage == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES && !INSTANCES.isEmpty()) {
            drawMeshes(event);
        } else if (stage == RenderLevelStageEvent.Stage.AFTER_PARTICLES && !DRAWN.isEmpty()) {
            drawOverlays(event);
        }
    }

    private static void drawMeshes(RenderLevelStageEvent event) {
        var camera = event.getCamera().getPosition();
        var modelView = event.getPoseStack().last().pose();
        var projection = event.getProjectionMatrix();
        var frustum = event.getFrustum();

        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        for (var instance : INSTANCES.closest(camera)) {
            var center = instance.center();
            if (!frustum.isVisible(new AABB(center, center).inflate(DysonSphereGeometry.VISUAL_REACH))) {
                continue;
            }
            DRAWN.add(instance);
            var frame = instance.frame();
            var offset = new Vector3f((float) (center.x - camera.x), (float) (center.y - camera.y),
                    (float) (center.z - camera.z));

            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            if (frame.radius > 0.01f && frame.brightness > 0.001f) {
                setStar(DysonShaders.star, instance, offset);
                draw(DysonMeshes.star(), DysonShaders.star, modelView, projection);
            }

            var ring = DysonShaders.ring;
            setStar(ring, instance, offset);
            setRings(ring, instance);
            for (int i = 0; i < RING_COUNT; i++) {
                if (frame.ringSweep[i] <= 0) continue;
                ring.safeGetUniform("RingMat").set(new Matrix4f().set(instance.rings()[i]));
                ring.safeGetUniform("RingIndex").set((float) i);
                ring.safeGetUniform("RingSweep").set(frame.ringSweep[i]);
                draw(DysonMeshes.ring(i), ring, modelView, projection);
            }

            if (frame.brightness > 0.001f || frame.shellIntensity > 0.001f) {
                RenderSystem.depthMask(false);
                MegastructureBlend.additive();
                setStar(DysonShaders.halo, instance, offset);
                setRings(DysonShaders.halo, instance);
                DysonShaders.halo.safeGetUniform("RingsSolid").set(solidRings(frame) ? 1f : 0f);
                DysonShaders.halo.safeGetUniform("Extent").set(frame.haloExtent);
                DysonShaders.halo.safeGetUniform("ShellRadius").set(frame.shellRadius);
                DysonShaders.halo.safeGetUniform("ShellIntensity").set(frame.shellIntensity);
                DysonShaders.halo.safeGetUniform("ShellNebula").set(frame.shellNebula);
                draw(MegastructureMeshes.quad(), DysonShaders.halo, modelView, projection);
            }
        }
        VertexBuffer.unbind();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        MegastructureBlend.restore();
    }

    private static void drawOverlays(RenderLevelStageEvent event) {
        var camera = event.getCamera().getPosition();
        var modelView = event.getPoseStack().last().pose();
        var projection = event.getProjectionMatrix();
        var main = Minecraft.getInstance().getMainRenderTarget();
        boolean rays = StarTConfig.INSTANCE.client.dysonGodRays && !Minecraft.useShaderTransparency();
        var shader = DysonShaders.godRays;

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        for (var instance : DRAWN) {
            var frame = instance.frame();
            var center = instance.center();
            var offset = new Vector3f((float) (center.x - camera.x), (float) (center.y - camera.y),
                    (float) (center.z - camera.z));
            float distance = offset.length();
            var theme = theme(frame);

            var overlay = new Vector3f();
            if (distance < frame.radius && frame.brightness > 0.001f) {
                overlay.set(theme).mul(0.35f * Math.min(frame.brightness, 1));
            }

            float intensity = 0;
            var clip = new Vector4f(offset, 1).mul(modelView).mul(projection);
            if (rays && clip.w > 0 && distance > frame.radius) {
                float u = clip.x / clip.w * 0.5f + 0.5f;
                float v = clip.y / clip.w * 0.5f + 0.5f;
                var front = new Vector4f(new Vector3f(offset).sub(new Vector3f(offset).normalize(frame.radius)), 1)
                        .mul(modelView).mul(projection);
                float corona = 4 * Math.max(frame.radius, 1) / distance * projection.m11() * 0.5f;
                float offscreen = Math.max(0, Math.max(Math.abs(u - 0.5f), Math.abs(v - 0.5f)) - 0.5f);
                intensity = frame.rays * frame.brightness * (1 - DysonAnimation.smoothstep(0, 0.5f, offscreen)) *
                        (1 - DysonAnimation.smoothstep(0.25f, 0.6f, corona));
                if (intensity > 0.001f) {
                    var capture = SceneCapture.capture();
                    shader.setSampler("SceneDepth", capture.getDepthTextureId());
                    shader.safeGetUniform("StarUv").set(u, v);
                    shader.safeGetUniform("StarDepth").set(front.w > 0 ? front.z / front.w * 0.5f + 0.5f : 1f);
                    shader.safeGetUniform("CoronaRadius").set(corona);
                    shader.safeGetUniform("Samples").set(StarTConfig.INSTANCE.client.dysonGodRaySamples);
                    shader.safeGetUniform("RayTint").set(new Vector3f(theme).mul(0.4f).add(0.6f, 0.6f, 0.6f));
                }
            }
            if (intensity <= 0.001f && overlay.lengthSquared() == 0) continue;
            shader.safeGetUniform("RayIntensity").set(Math.max(intensity, 0));
            shader.safeGetUniform("Overlay").set(overlay);
            shader.safeGetUniform("ScreenSize").set((float) main.width, (float) main.height);
            MegastructureBlend.additive();
            draw(MegastructureMeshes.quad(), shader, IDENTITY, IDENTITY);
        }
        VertexBuffer.unbind();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        MegastructureBlend.restore();
    }

    private static boolean solidRings(DysonAnimation.Frame frame) {
        if (frame.ringBreak > 0) return false;
        for (float sweep : frame.ringSweep) {
            if (sweep < 1) return false;
        }
        return true;
    }

    private static Vector3f theme(DysonAnimation.Frame frame) {
        float max = Math.max(Math.max(frame.red, frame.green), Math.max(frame.blue, 1e-4f));
        return new Vector3f(frame.red / max, frame.green / max, frame.blue / max);
    }

    private static void setStar(ShaderInstance shader, Instance instance, Vector3f offset) {
        var frame = instance.frame();
        shader.safeGetUniform("CenterOffset").set(offset);
        shader.safeGetUniform("Radius").set(frame.radius);
        shader.safeGetUniform("Color").set(frame.red, frame.green, frame.blue);
        shader.safeGetUniform("Time").set(instance.seconds());
        shader.safeGetUniform("Activity").set(frame.activity);
        shader.safeGetUniform("Giant").set(frame.giant);
        shader.safeGetUniform("Heat").set(frame.heat);
        shader.safeGetUniform("Brightness").set(frame.brightness);
        shader.safeGetUniform("Protostar").set(frame.protostar);
        shader.safeGetUniform("Detail").set((float) StarTConfig.INSTANCE.client.dysonStarDetail.ordinal());
        shader.safeGetUniform("Seed").set(instance.seed());
    }

    private static void setRings(ShaderInstance shader, Instance instance) {
        var frame = instance.frame();
        for (int i = 0; i < RING_COUNT; i++) {
            shader.safeGetUniform("RingNormal" + i).set(instance.rings()[i].getColumn(2, new Vector3f()));
        }
        shader.safeGetUniform("RingBase").set(DysonSphereGeometry.RING_BASE_RADIUS);
        shader.safeGetUniform("RingSpacing").set(DysonSphereGeometry.RING_SPACING);
        shader.safeGetUniform("RingWidth").set(DysonSphereGeometry.RING_WIDTH);
        shader.safeGetUniform("RingThickness").set(DysonSphereGeometry.RING_THICKNESS);
        shader.safeGetUniform("RingGlow").set(frame.ringGlow);
        shader.safeGetUniform("RingHeat").set(frame.ringHeat);
        shader.safeGetUniform("RingAmbient").set(instance.ambient());
        shader.safeGetUniform("RingBreak").set(frame.ringBreak);
    }

    private static void draw(VertexBuffer buffer, ShaderInstance shader, Matrix4f modelView, Matrix4f projection) {
        buffer.bind();
        buffer.drawWithShader(modelView, projection, shader);
    }
}
