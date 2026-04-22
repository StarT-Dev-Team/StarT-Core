package com.startechnology.start_core.machine.komaru.client;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.startechnology.start_core.machine.komaru.StarTKomaruFrameMachine;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KomaruRenderer extends DynamicRender<StarTKomaruFrameMachine, KomaruRenderer> {

    public static final Codec<KomaruRenderer> CODEC = Codec.unit(KomaruRenderer::new);
    public static final DynamicRenderType<StarTKomaruFrameMachine, KomaruRenderer> TYPE = new DynamicRenderType<>(KomaruRenderer.CODEC);

    public KomaruRenderer() {}

    @Override
    public @NotNull DynamicRenderType<StarTKomaruFrameMachine, KomaruRenderer> getType() {
        return TYPE;
    }

    @Override
    public @NotNull List<BakedQuad> getRenderQuads(@Nullable StarTKomaruFrameMachine machine, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, @Nullable BlockState blockState, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData modelData, @Nullable RenderType renderType) {
        return super.getRenderQuads(machine, level, pos, blockState, side, rand, modelData, renderType);
    }

    @Override
    public void render(@NotNull StarTKomaruFrameMachine machine, float partialTicks, @NotNull PoseStack stack, @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        HookLevelRenderer.COLLECTED_RENDERS.add(machine);
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull StarTKomaruFrameMachine machine) {
        return true;
    }

    @Override
    public boolean shouldRender(@NotNull StarTKomaruFrameMachine machine, @NotNull Vec3 cameraPos) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 2048;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull StarTKomaruFrameMachine machine) {
        var front = machine.getFrontFacing();
        var upwards = machine.getUpwardsFacing();
        var flipped = machine.isFlipped();
        var back = RelativeDirection.BACK.getRelative(front, upwards, flipped);
        var up = RelativeDirection.UP.getRelative(front, upwards, flipped);

        var centerOffset = 31;
        var beamHeight = 133;

        var center = new Vec3i(back.getStepX() * centerOffset, back.getStepY() * centerOffset, back.getStepZ() * centerOffset)
                .offset(up.getStepX() * 2, up.getStepY() * 2, up.getStepZ() * 2);
        var top = center.offset(up.getStepX() * beamHeight, up.getStepY() * beamHeight, up.getStepZ() * beamHeight);

        var aabb = new AABB(center.getX() + 0.5f, center.getY() + 0.5f, center.getZ() + 0.5f, top.getX() + 0.5f, top.getY() + 0.5f, top.getZ() + 0.5f);
        aabb = aabb.inflate(128, 20, 128);

        return aabb;
    }

}
