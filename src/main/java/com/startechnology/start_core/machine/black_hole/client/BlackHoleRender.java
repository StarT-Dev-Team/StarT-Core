package com.startechnology.start_core.machine.black_hole.client;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.startechnology.start_core.StarTConfig;
import com.startechnology.start_core.client.megastructure.RenderTier;
import com.startechnology.start_core.client.megastructure.ShaderpackCompat;
import com.startechnology.start_core.machine.black_hole.BlackHoleBalance;
import com.startechnology.start_core.machine.black_hole.BlackHoleGeneratorMachine;
import com.startechnology.start_core.machine.black_hole.BlackHolePhase;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static com.startechnology.start_core.machine.black_hole.StarTBlackHoleMachines.STABILIZER_COUNT;
import static com.startechnology.start_core.machine.black_hole.StarTBlackHoleMachines.STABILIZER_DISTANCE;

public class BlackHoleRender extends DynamicRender<BlackHoleGeneratorMachine, BlackHoleRender> {

    public static final Codec<BlackHoleRender> CODEC = Codec.unit(BlackHoleRender::new);
    public static final DynamicRenderType<BlackHoleGeneratorMachine, BlackHoleRender> TYPE = new DynamicRenderType<>(
            CODEC);

    @Override
    public @NotNull DynamicRenderType<BlackHoleGeneratorMachine, BlackHoleRender> getType() {
        return TYPE;
    }

    @Override
    public void render(@NotNull BlackHoleGeneratorMachine machine, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        var minecraft = Minecraft.getInstance();
        var level = machine.getLevel();
        if (level == null || level != minecraft.level) return;
        var tier = BlackHoleRenderManager.tier();
        if (tier == RenderTier.OFF) return;
        if (ShaderpackCompat.isRenderingShadowPass()) return;

        var phase = machine.getPhase();
        if (phase == BlackHolePhase.IDLE && !machine.isFormed()) return;
        boolean lensed = tier == RenderTier.FULL;
        if (!lensed && !BlackHoleRenderManager.claimInstance()) return;

        var clientState = BlackHoleClientState.of(machine);
        clientState.update(machine);
        float worldTime = level.getGameTime() % 72000L + partialTick;
        var frame = BlackHoleAnimation.compute(machine, clientState,
                BlackHoleClientState.phaseTime(machine, partialTick), worldTime);

        var center = machine.getCenter();
        var camera = minecraft.gameRenderer.getMainCamera().getPosition();
        var view = new Vector3f((float) (camera.x - center.getX() - 0.5), (float) (camera.y - center.getY() - 0.5),
                (float) (camera.z - center.getZ() - 0.5));
        var axes = new Vector3f[STABILIZER_COUNT];
        for (int i = 0; i < STABILIZER_COUNT; i++) {
            axes[i] = machine.getStabilizerDirection(i).step();
        }

        var basis = BlackHoleAnimation.diskBasis(axes, frame.diskTilt);

        if (lensed && (frame.horizon > 0 || frame.diskIntensity > 0 || frame.flare > 0 || frame.shockIntensity > 0)) {
            float lensing = (float) BlackHoleBalance.lensingRadius(BlackHoleBalance.horizonRadius(clientState.mass));
            BlackHoleRenderManager.queue(new BlackHoleRenderManager.Lens(frame, Vec3.atCenterOf(center), basis,
                    lensing, worldTime / 20f));
        }

        var offset = center.subtract(machine.getPos());
        poseStack.pushPose();
        poseStack.translate(offset.getX() + 0.5, offset.getY() + 0.5, offset.getZ() + 0.5);
        new BlackHoleFallbackRenderer(poseStack.last(), buffer, frame, view, axes, basis, worldTime)
                .render(StarTConfig.INSTANCE.client.blackHoleBeams, !lensed);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull BlackHoleGeneratorMachine machine) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return StarTConfig.INSTANCE.client.blackHoleViewDistance;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull BlackHoleGeneratorMachine machine) {
        var center = Vec3.atCenterOf(machine.getCenter());
        return new AABB(center, center).inflate(STABILIZER_DISTANCE + 4);
    }
}
