package com.startechnology.start_core.machine.neutron_star.client;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.startechnology.start_core.StarTConfig;
import com.startechnology.start_core.client.megastructure.EmissiveQuads;
import com.startechnology.start_core.client.megastructure.RenderTier;
import com.startechnology.start_core.client.megastructure.ShaderpackCompat;
import com.startechnology.start_core.machine.black_hole.client.BlackHoleRenderTypes;
import com.startechnology.start_core.machine.neutron_star.NeutronStarForgeMachine;
import com.startechnology.start_core.machine.neutron_star.NeutronStarGeometry;
import com.startechnology.start_core.machine.neutron_star.NeutronStarPhase;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static com.startechnology.start_core.machine.neutron_star.NeutronStarGeometry.COLLECTOR_DISTANCE;

public class NeutronStarRender extends DynamicRender<NeutronStarForgeMachine, NeutronStarRender> {

    public static final Codec<NeutronStarRender> CODEC = Codec.unit(NeutronStarRender::new);
    public static final DynamicRenderType<NeutronStarForgeMachine, NeutronStarRender> TYPE = new DynamicRenderType<>(
            CODEC);

    private static final float DISH_IDLE_GLOW = 0.12f;

    @Override
    public @NotNull DynamicRenderType<NeutronStarForgeMachine, NeutronStarRender> getType() {
        return TYPE;
    }

    @Override
    public void render(@NotNull NeutronStarForgeMachine machine, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        var minecraft = Minecraft.getInstance();
        var level = machine.getLevel();
        if (level == null || level != minecraft.level) return;
        float gameTime = level.getGameTime() + partialTick;
        var state = NeutronStarClientState.of(machine);
        state.update(machine, gameTime);
        var tier = NeutronStarRenderManager.tier();
        if (tier == RenderTier.OFF) return;
        if (ShaderpackCompat.isRenderingShadowPass()) return;

        var phase = machine.getPhase();
        var frame = state.frame;
        float seconds = (level.getGameTime() % 72000L + partialTick) / 20f;
        var center = machine.getCenter();
        if (phase != NeutronStarPhase.IDLE) {
            float phaseTime = Math.max(0, gameTime - machine.getPhaseStartGameTime());
            NeutronStarAnimation.compute(frame, phase, phaseTime, seconds, gameTime - machine.getGlitchStartGameTime(),
                    state);
            state.field.update(state.spinAngle, 0.2f, frame.twist);
            if (tier == RenderTier.FULL) {
                NeutronStarRenderManager.queue(new NeutronStarRenderManager.Instance(center, frame, state.field,
                        state.surfaceTime, state.beadPhase, seconds));
            } else if (NeutronStarRenderManager.claimInstance()) {
                var offset = center.subtract(Vec3.atLowerCornerOf(machine.getPos()));
                var view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().subtract(center);
                poseStack.pushPose();
                poseStack.translate(offset.x, offset.y, offset.z);
                new NeutronStarFallbackRenderer(poseStack.last(), buffer, frame, state.field, state.beadPhase,
                        view.toVector3f()).render();
                poseStack.popPose();
            }
        }
        if (machine.isFormed() || phase != NeutronStarPhase.IDLE) dishGlows(machine, state, phase, poseStack, buffer);
    }

    private static void dishGlows(NeutronStarForgeMachine machine, NeutronStarClientState state,
                                  NeutronStarPhase phase, PoseStack poseStack, MultiBufferSource buffer) {
        var frame = state.frame;
        var origin = Vec3.atLowerCornerOf(machine.getPos());
        var center = machine.getCenter().subtract(origin).toVector3f();
        var camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().subtract(origin);
        var quads = new EmissiveQuads(poseStack.last(), buffer, camera.toVector3f());
        quads.setBody(false);
        var glow = quads.consumer(BlackHoleRenderTypes.GLOW_TEXTURE);
        float max = Math.max(Math.max(state.red, state.green), Math.max(state.blue, 1e-4f));
        float r = Mth.lerp(0.4f, state.red / max, 1), g = Mth.lerp(0.4f, state.green / max, 1);
        float b = Mth.lerp(0.4f, state.blue / max, 1);
        float lift = COLLECTOR_DISTANCE - 0.5f;
        for (int side = -1; side <= 1; side += 2) {
            quads.billboard(glow, new Vector3f(center).add(0, side * lift, 0), 3, r, g, b, DISH_IDLE_GLOW);
        }
        if (phase == NeutronStarPhase.IDLE) return;

        float reach = NeutronStarAnimation.smoothstep(0.9f, 1, frame.jetLength / NeutronStarAnimation.JET_LENGTH);
        float level = Math.min(frame.jetWeight * reach + frame.flash * 0.3f * reach, 1.6f);
        if (level <= 0.001f) return;
        var axis = state.field.jetAxis(new Vector3f());
        if (Math.abs(axis.y) < 0.2f) return;
        float flicker = 0.9f + 0.1f * Mth.sin(state.spinAngle * 23);
        for (int side = -1; side <= 1; side += 2) {
            var impact = new Vector3f(axis).mul(side * COLLECTOR_DISTANCE / axis.y);
            int rise = NeutronStarGeometry.dishRise(Math.round(impact.x), Math.round(impact.z));
            impact.mul((COLLECTOR_DISTANCE - rise - 0.5f) / COLLECTOR_DISTANCE).add(center);
            quads.billboard(glow, impact, 2.5f + 2.5f * level, r, g, b, level * flicker);
            quads.billboard(glow, impact, 1.2f + level, 1, 1, 1, 0.6f * level * flicker);
        }
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull NeutronStarForgeMachine machine) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return StarTConfig.INSTANCE.client.neutronStarViewDistance;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull NeutronStarForgeMachine machine) {
        return NeutronStarRenderManager.bounds(machine.getCenter());
    }
}
