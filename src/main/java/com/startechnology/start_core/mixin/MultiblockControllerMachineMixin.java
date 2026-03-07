package com.startechnology.start_core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.startechnology.start_core.machine.redstone.IStarTRedstoneIndicatorMachine;

@Mixin(value = MultiblockControllerMachine.class, remap = false)
public abstract class MultiblockControllerMachineMixin {

    @Inject(method = "onStructureFormed", at = @At("TAIL"))
    private void onStructureFormed(CallbackInfo ci) {
        if (this instanceof IStarTRedstoneIndicatorMachine machine) {
            machine.onRedstoneStructureFormed();
        }
    }

    @Inject(method = "onStructureInvalid", at = @At("TAIL"))
    private void onStructureInvalid(CallbackInfo ci) {
        if (this instanceof IStarTRedstoneIndicatorMachine machine) {
            machine.onRedstoneStructureInvalid();
        }
    }
}