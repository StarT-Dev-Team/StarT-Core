package com.startechnology.start_core.machine.neutron_star.client;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.startechnology.start_core.StarTConfig;
import com.startechnology.start_core.client.megastructure.InstanceCollector;
import com.startechnology.start_core.client.megastructure.MegastructureBlend;
import com.startechnology.start_core.client.megastructure.MegastructureMeshes;
import com.startechnology.start_core.client.megastructure.RenderTier;
import com.startechnology.start_core.client.megastructure.SceneCapture;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.ArrayList;
import java.util.List;

import static com.startechnology.start_core.machine.neutron_star.NeutronStarGeometry.HALO_BOUND;
import static com.startechnology.start_core.machine.neutron_star.NeutronStarGeometry.HORIZONTAL_REACH;
import static com.startechnology.start_core.machine.neutron_star.NeutronStarGeometry.JET_TILT_MAX;
import static com.startechnology.start_core.machine.neutron_star.NeutronStarGeometry.SCALE;
import static com.startechnology.start_core.machine.neutron_star.NeutronStarGeometry.VERTICAL_REACH;
import static com.startechnology.start_core.machine.neutron_star.client.NeutronStarField.LOOP_COUNT;

public final class NeutronStarRenderManager {

    public record Instance(Vec3 center, NeutronStarAnimation.Frame frame, NeutronStarField field, float surfaceTime,
                           float beadPhase, float seconds) {}

    private static final InstanceCollector<Instance> INSTANCES = new InstanceCollector<>(Instance::center,
            () -> StarTConfig.INSTANCE.client.neutronStarMaxInstances);
    private static final List<Instance> VISIBLE = new ArrayList<>();
    private static final Matrix3f IDENTITY = new Matrix3f();
    private static final String[] LOOP_MATS = new String[LOOP_COUNT];
    private static final int[] DETAIL_STEPS = { 48, 72, 100 };
    private static final float JET_GLOW_MARGIN = 0.3f;

    static {
        for (int i = 0; i < LOOP_COUNT; i++) LOOP_MATS[i] = "LoopMat" + i;
    }

    private NeutronStarRenderManager() {}

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, NeutronStarRenderManager::onRenderLevelStage);
    }

    public static RenderTier tier() {
        return RenderTier.resolve(StarTConfig.INSTANCE.client.neutronStarRenderMode, NeutronStarShaders.loaded());
    }

    public static boolean claimInstance() {
        return INSTANCES.claim();
    }

    public static void queue(Instance instance) {
        INSTANCES.add(instance);
    }

    public static AABB bounds(Vec3 center) {
        return new AABB(center, center).inflate(HORIZONTAL_REACH, VERTICAL_REACH, HORIZONTAL_REACH);
    }

    private static void onRenderLevelStage(RenderLevelStageEvent event) {
        var stage = event.getStage();
        if (stage == RenderLevelStageEvent.Stage.AFTER_SKY) {
            INSTANCES.clear();
            return;
        }
        if (stage != RenderLevelStageEvent.Stage.AFTER_PARTICLES || INSTANCES.isEmpty() ||
                !NeutronStarShaders.loaded()) {
            return;
        }
        draw(event);
    }

    private static void draw(RenderLevelStageEvent event) {
        var camera = event.getCamera().getPosition();
        var frustum = event.getFrustum();
        VISIBLE.clear();
        for (var instance : INSTANCES.closest(camera)) {
            if (frustum.isVisible(bounds(instance.center()))) VISIBLE.add(instance);
        }
        if (VISIBLE.isEmpty()) return;

        var shader = NeutronStarShaders.star;
        var modelView = event.getPoseStack().last().pose();
        var projection = event.getProjectionMatrix();
        setShared(shader, projection);

        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GlConst.GL_ALWAYS);
        RenderSystem.depthMask(true);
        RenderSystem.disableCull();
        MegastructureBlend.premultiplied();
        for (int i = VISIBLE.size() - 1; i >= 0; i--) {
            if (i < VISIBLE.size() - 1) SceneCapture.invalidate();
            shader.setSampler("SceneDepth", SceneCapture.capture().getDepthTextureId());
            setInstance(shader, VISIBLE.get(i), camera);
            var quad = MegastructureMeshes.quad();
            quad.bind();
            quad.drawWithShader(modelView, projection, shader);
        }
        VertexBuffer.unbind();
        SceneCapture.invalidate();
        RenderSystem.depthFunc(GlConst.GL_LEQUAL);
        RenderSystem.enableCull();
        MegastructureBlend.restore();
        if (Minecraft.useShaderTransparency()) {
            var particles = Minecraft.getInstance().levelRenderer.getParticlesTarget();
            if (particles != null) particles.bindWrite(false);
        }
    }

    private static void setShared(ShaderInstance shader, Matrix4f projection) {
        shader.safeGetUniform("InvProjMat").set(new Matrix4f(projection).invert());
        shader.safeGetUniform("Scale").set(SCALE);
        shader.safeGetUniform("Basis").set(IDENTITY);
        shader.safeGetUniform("SurfaceFlow").set(0.5f, -0.3f, 0.4f);
        shader.safeGetUniform("LoopShape").set(0.17f, 0.2f, 0.6f, 0.5f);
        shader.safeGetUniform("LoopMinor").set(0.0001f);
        shader.safeGetUniform("BeadRadius").set(0.004f);
        shader.safeGetUniform("GlowFalloff").set(0.022f, 0.19f, 0.019f, 0.022f);
        shader.safeGetUniform("Steps").set(DETAIL_STEPS[StarTConfig.INSTANCE.client.neutronStarDetail.ordinal()]);
        shader.safeGetUniform("SurfDist").set(0.001f);
        shader.safeGetUniform("Gain").set(0.02f, 0.0045f);
        shader.safeGetUniform("HaloBound").set(HALO_BOUND);
    }

    private static void setInstance(ShaderInstance shader, Instance instance, Vec3 camera) {
        var f = instance.frame();
        var field = instance.field();
        var center = instance.center();
        shader.safeGetUniform("CenterOffset").set((float) (center.x - camera.x), (float) (center.y - camera.y),
                (float) (center.z - camera.z));
        shader.safeGetUniform("StarMat").set(field.star);
        shader.safeGetUniform("JetMat").set(field.jet);
        for (int i = 0; i < LOOP_COUNT; i++) shader.safeGetUniform(LOOP_MATS[i]).set(field.loops[i]);
        shader.safeGetUniform("LoopGrowA").set(f.grow[0], f.grow[1], f.grow[2], f.grow[3]);
        shader.safeGetUniform("LoopGrowB").set(f.grow[4], f.grow[5], f.grow[6], f.grow[7]);
        shader.safeGetUniform("SurfaceTime").set(instance.surfaceTime());
        shader.safeGetUniform("StarRadius").set(f.starRadius);
        shader.safeGetUniform("Surface").set(f.surfaceAmplitude, 20f, 4f, 1.5f);
        shader.safeGetUniform("JetWidth").set(f.jetWidthBase, f.jetWidthSlope);
        shader.safeGetUniform("JetLength").set(f.jetLength);
        shader.safeGetUniform("JetBoundRadius").set(f.jetLength * (float) Math.tan(JET_TILT_MAX) + f.jetWidthBase +
                f.jetWidthSlope * f.jetLength + JET_GLOW_MARGIN);
        shader.safeGetUniform("BeadPhase").set(instance.beadPhase());
        shader.safeGetUniform("GlowWeight").set(f.starWeight, f.loopWeight, f.beadWeight, f.jetWeight);
        shader.safeGetUniform("HitColor").set(f.red, f.green, f.blue);
        shader.safeGetUniform("MissColor").set(f.red, f.green * 0.8f, f.blue);
        shader.safeGetUniform("Flash").set(f.flash);
        shader.safeGetUniform("ShellRadius").set(f.shellRadius);
        shader.safeGetUniform("ShellIntensity").set(f.shellIntensity);
        shader.safeGetUniform("DarkCore").set(f.darkCore);
        shader.safeGetUniform("Time").set(instance.seconds());
    }
}
