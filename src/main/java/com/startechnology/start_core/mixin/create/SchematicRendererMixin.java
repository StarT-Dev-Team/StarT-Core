package com.startechnology.start_core.mixin.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.schematics.client.SchematicRenderer;
import com.startechnology.start_core.mixin.ccl.BlockRenderingRegistryAccessor;
import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.createmod.catnip.render.ShadedBlockSbbBuilder;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = SchematicRenderer.class, remap = false)
public class SchematicRendererMixin {

    @Unique
    private List<BakedQuad> start_core$backedQuads;

    @Unique
    private QuadBakingVertexConsumer start_core$builder;

    @Unique
    private PoseStack.Pose start_core$identityPose;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initQuadBakingVC(SchematicLevel world, CallbackInfo ci) {
        start_core$backedQuads = new ArrayList<>();
        start_core$builder = new QuadBakingVertexConsumer(start_core$backedQuads::add);
        start_core$identityPose = new PoseStack().last();
    }

    @SuppressWarnings("removal")
    @WrapOperation(method = "drawLayer", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ChunkRenderTypeSet;contains(Lnet/minecraft/client/renderer/RenderType;)Z"))
    private boolean forwardCBLibRender(ChunkRenderTypeSet instance, RenderType layer, Operation<Boolean> original,
                                       @Local(name = "renderWorld") SchematicLevel level, @Local(name = "poseStack") PoseStack stack,
                                       @Local(name = "sbbBuilder") ShadedBlockSbbBuilder sbbBuilder,
                                       @Local(name = "random") RandomSource rand,
                                       @Local(name = "state") BlockState state, @Local(name = "pos") BlockPos pos,
                                       @Local(name = "localPos") BlockPos localPos) {
        var renderer = BlockRenderingRegistryAccessor.invokeFindFor(state.getBlock(), e -> e.canHandleBlock(level, pos, state, layer));
        if (renderer != null) {
            start_core$backedQuads.clear();
            start_core$builder.setShade(true);

            stack.pushPose();
            stack.translate((float)localPos.getX(), (float)localPos.getY(), (float)localPos.getZ());
            var brightness = LevelRenderer.getLightColor(level, state, localPos);
            renderer.renderBlock(state, pos, level, stack, start_core$builder, rand, ModelData.EMPTY, layer);
            for (var baked : start_core$backedQuads) {
                sbbBuilder.putBulkData(start_core$identityPose, baked, 1.0f, 1.0f, 1.0f, brightness, OverlayTexture.NO_OVERLAY);
            }
            stack.popPose();

            return false;
        }

        return original.call(instance, layer);
    }

}
