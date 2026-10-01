package com.startechnology.start_core.machine.dyson_sphere.client;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.startechnology.start_core.StarTConfig;
import com.startechnology.start_core.client.megastructure.EmissiveQuads;
import com.startechnology.start_core.client.megastructure.RenderTier;
import com.startechnology.start_core.client.megastructure.ShaderpackCompat;
import com.startechnology.start_core.machine.black_hole.client.BlackHoleRenderTypes;
import com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry;
import com.startechnology.start_core.machine.dyson_sphere.DysonSphereMachine;
import com.startechnology.start_core.machine.dyson_sphere.RingAssembly;
import com.startechnology.start_core.machine.dyson_sphere.StellarPhase;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.PYLON_COUNT;

public class DysonSphereRender extends DynamicRender<DysonSphereMachine, DysonSphereRender> {

    public static final Codec<DysonSphereRender> CODEC = Codec.unit(DysonSphereRender::new);
    public static final DynamicRenderType<DysonSphereMachine, DysonSphereRender> TYPE = new DynamicRenderType<>(
            CODEC);

    @Override
    public @NotNull DynamicRenderType<DysonSphereMachine, DysonSphereRender> getType() {
        return TYPE;
    }

    @Override
    public void render(@NotNull DysonSphereMachine machine, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        var minecraft = Minecraft.getInstance();
        var level = machine.getLevel();
        if (level == null || level != minecraft.level) return;
        var state = DysonClientState.of(machine);
        state.update(machine);
        state.keepHum(machine);
        var tier = DysonSphereRenderManager.tier();
        if (tier == RenderTier.OFF) return;
        if (ShaderpackCompat.isRenderingShadowPass()) return;
        if (machine.getPhase() == StellarPhase.IDLE && machine.getAssembly() == RingAssembly.NONE) return;
        boolean full = tier == RenderTier.FULL;
        if (!full && !DysonSphereRenderManager.claimInstance()) return;

        long gameTime = level.getGameTime();
        float phaseTime = Math.max(0, gameTime + partialTick - machine.getPhaseStartGameTime());
        float assemblyTime = Math.max(0, gameTime + partialTick - machine.getAssemblyStartGameTime());
        float seconds = (gameTime % 72000L + partialTick) / 20f;
        var frame = DysonAnimation.compute(machine, state, phaseTime, assemblyTime, seconds);
        var center = Vec3.atCenterOf(machine.getCenter());
        float seed = seed(machine);
        var rings = DysonAnimation.ringFrames(seed, state.precession, state.spin);
        float ambient = ambient(minecraft.level, machine, partialTick);

        if (full) {
            DysonSphereRenderManager.queue(new DysonSphereRenderManager.Instance(center, frame, rings, seconds, seed,
                    ambient));
        } else {
            var offset = machine.getCenter().subtract(machine.getPos());
            var view = minecraft.gameRenderer.getMainCamera().getPosition().subtract(center).toVector3f();
            poseStack.pushPose();
            poseStack.translate(offset.getX() + 0.5, offset.getY() + 0.5, offset.getZ() + 0.5);
            new DysonFallbackRenderer(poseStack.last(), buffer, frame, rings, view, seconds, seed, ambient).render();
            poseStack.popPose();
        }
        beams(machine, frame, center, poseStack, buffer, seconds);
    }

    private static void beams(DysonSphereMachine machine, DysonAnimation.Frame frame, Vec3 center,
                              PoseStack poseStack, MultiBufferSource buffer, float seconds) {
        if (frame.pylonBeams <= 0.001f && frame.anchorBeam <= 0.001f) return;
        var origin = Vec3.atLowerCornerOf(machine.getPos());
        var camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().subtract(origin);
        var quads = new EmissiveQuads(poseStack.last(), buffer, camera.toVector3f());
        float max = Math.max(Math.max(frame.red, frame.green), Math.max(frame.blue, 1e-4f));
        float radius = Math.max(frame.radius, 0.5f);

        for (int pass = 0; pass < 2; pass++) {
            quads.setBody(pass == 0);
            var consumer = quads.consumer(BlackHoleRenderTypes.BEAM_TEXTURE);
            for (int i = 0; i <= PYLON_COUNT; i++) {
                boolean anchor = i == PYLON_COUNT;
                float level = anchor ? frame.anchorBeam : frame.pylonBeams;
                if (level <= 0.001f) continue;
                var tip = anchor ? machine.getAnchorTip() : machine.getPylonTip(i);
                var end = center.add(tip.subtract(center).normalize().scale(radius));
                quads.beam(consumer, tip.subtract(origin).toVector3f(), end.subtract(origin).toVector3f(),
                        frame.red / max, frame.green / max, frame.blue / max, level, seconds + i * 0.37f);
            }
        }

        quads.setBody(false);
        var glow = quads.consumer(BlackHoleRenderTypes.GLOW_TEXTURE);
        for (int i = 0; i <= PYLON_COUNT; i++) {
            boolean anchor = i == PYLON_COUNT;
            float level = anchor ? frame.anchorBeam : frame.pylonBeams;
            if (level <= 0.001f) continue;
            float flicker = 0.85f + 0.15f * Mth.sin(seconds * 7.3f + i * 2.1f);
            var tip = anchor ? machine.getAnchorTip() : machine.getPylonTip(i);
            var end = center.add(tip.subtract(center).normalize().scale(radius));
            quads.billboard(glow, tip.subtract(origin).toVector3f(), 1.6f + level, frame.red / max,
                    frame.green / max, frame.blue / max, level * flicker);
            quads.billboard(glow, end.subtract(origin).toVector3f(), 2.5f + 1.5f * level, Mth.lerp(0.5f,
                    frame.red / max, 1), Mth.lerp(0.5f, frame.green / max, 1), Mth.lerp(0.5f, frame.blue / max, 1),
                    0.8f * level * flicker);
        }
    }

    private static float ambient(ClientLevel level, DysonSphereMachine machine, float partialTick) {
        var pos = machine.getCenter();
        float sky = level.getBrightness(LightLayer.SKY, pos) / 15f * level.getSkyDarken(partialTick);
        float block = level.getBrightness(LightLayer.BLOCK, pos) / 15f;
        return Math.max(sky, block) * 0.6f;
    }

    private static float seed(DysonSphereMachine machine) {
        long hash = machine.getPos().asLong() * 0x9E3779B97F4A7C15L;
        return (hash >>> 40) / (float) (1 << 24) * 6.2831855f;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull DysonSphereMachine machine) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return StarTConfig.INSTANCE.client.dysonViewDistance;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull DysonSphereMachine machine) {
        var center = Vec3.atCenterOf(machine.getCenter());
        return new AABB(center, center).inflate(DysonSphereGeometry.VISUAL_REACH);
    }
}
