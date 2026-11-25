package com.startechnology.start_core.machine.dimensional_destabiliser.client;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.startechnology.start_core.StarTConfig;
import com.startechnology.start_core.client.megastructure.RenderTier;
import com.startechnology.start_core.client.megastructure.ShaderpackCompat;
import com.startechnology.start_core.machine.dimensional_destabiliser.DimensionalDestabiliserMachine;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.AABB;

public class RiftRender extends DynamicRender<DimensionalDestabiliserMachine, RiftRender> {

    public static final Codec<RiftRender> CODEC = Codec.unit(RiftRender::new);
    public static final DynamicRenderType<DimensionalDestabiliserMachine, RiftRender> TYPE = new DynamicRenderType<>(
            CODEC);

    @Override
    public @NotNull DynamicRenderType<DimensionalDestabiliserMachine, RiftRender> getType() {
        return TYPE;
    }

    @Override
    public void render(@NotNull DimensionalDestabiliserMachine machine, float partialTick,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight,
                       int packedOverlay) {
        var minecraft = Minecraft.getInstance();
        var level = machine.getLevel();
        if (level == null || level != minecraft.level) return;
        if (RiftRenderManager.tier() != RenderTier.FULL) return;
        if (ShaderpackCompat.isRenderingShadowPass()) return;
        var state = RiftClientState.of(machine);
        state.update(machine, level.getGameTime(), partialTick);
        var camera = minecraft.gameRenderer.getMainCamera().getPosition();
        if (state.state != RiftAnimation.State.NONE &&
                state.centre().closerThan(camera, RiftRenderManager.FULL_DISTANCE)) {
            RiftRenderManager.queue(state);
        }
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull DimensionalDestabiliserMachine machine) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return StarTConfig.INSTANCE.client.riftViewDistance;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull DimensionalDestabiliserMachine machine) {
        return RiftClientState.of(machine).bounds(machine);
    }
}
