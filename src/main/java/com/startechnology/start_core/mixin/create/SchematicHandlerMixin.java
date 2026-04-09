package com.startechnology.start_core.mixin.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.schematics.client.SchematicHandler;
import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SchematicHandler.class, remap = false)
public class SchematicHandlerMixin {

    @Inject(method = "setupRenderer", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/schematics/client/SchematicHandler;fixControllerBlockEntities(Lnet/createmod/catnip/levelWrappers/SchematicLevel;)V", ordinal = 0))
    private void injectSetupRenderer(CallbackInfo ci, @Local(name = "w") SchematicLevel w) {
        StructureTransform transform = new StructureTransform(BlockPos.ZERO, Direction.Axis.Y, Rotation.NONE, Mirror.NONE);
        for(BlockEntity be : w.getRenderedBlockEntities()) {
            transform.apply(be);
        }
    }

    @Inject(method = "setupRenderer", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/StructureTransform;apply(Lnet/minecraft/world/level/block/entity/BlockEntity;)V", ordinal = 0))
    private void injectTransformApplyFB(CallbackInfo ci, @Local(name = "be") BlockEntity be, @Local(name = "wMirroredFB") SchematicLevel level) {
        be.setLevel(level);
    }

    @Inject(method = "setupRenderer", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/StructureTransform;apply(Lnet/minecraft/world/level/block/entity/BlockEntity;)V", ordinal = 1))
    private void injectTransformApplyLR(CallbackInfo ci, @Local(name = "be") BlockEntity be, @Local(name = "wMirroredLR") SchematicLevel level) {
        be.setLevel(level);
    }
}
