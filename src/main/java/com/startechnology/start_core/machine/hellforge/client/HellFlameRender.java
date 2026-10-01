package com.startechnology.start_core.machine.hellforge.client;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.startechnology.start_core.StarTConfig;
import com.startechnology.start_core.client.megastructure.RenderTier;
import com.startechnology.start_core.client.megastructure.ShaderpackCompat;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachine;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class HellFlameRender extends DynamicRender<StarTHellForgeMachine, HellFlameRender> {

    public static final Codec<HellFlameRender> CODEC = Codec.unit(HellFlameRender::new);
    public static final DynamicRenderType<StarTHellForgeMachine, HellFlameRender> TYPE = new DynamicRenderType<>(
            CODEC);

    @Override
    public @NotNull DynamicRenderType<StarTHellForgeMachine, HellFlameRender> getType() {
        return TYPE;
    }

    @Override
    public void render(@NotNull StarTHellForgeMachine machine, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        var minecraft = Minecraft.getInstance();
        var level = machine.getLevel();
        if (level == null || level != minecraft.level) return;
        var tier = HellFlameRenderManager.tier();
        if (tier == RenderTier.OFF) return;
        if (ShaderpackCompat.isRenderingShadowPass()) return;
        var state = HellFlameClientState.of(machine);
        state.update(machine, level.getGameTime(), partialTick);
        if (state.state == HellFlameAnimation.State.NONE) return;
        if (tier == RenderTier.FULL) {
            HellFlameRenderManager.queue(state);
        } else {
            HellFlameFallbackRenderer.render(state, poseStack, buffer, Vec3.atLowerCornerOf(machine.getPos()));
        }
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull StarTHellForgeMachine machine) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return StarTConfig.INSTANCE.client.hellFlameViewDistance;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull StarTHellForgeMachine machine) {
        return HellFlameClientState.of(machine).bounds(machine);
    }
}
