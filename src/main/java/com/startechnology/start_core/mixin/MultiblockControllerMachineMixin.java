package com.startechnology.start_core.mixin;

import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.startechnology.start_core.machine.redstone.IMultiblockControllerMachineRedstoneIndicators;
import com.startechnology.start_core.machine.redstone.IRedstoneIndicatorMachine;
import com.startechnology.start_core.machine.redstone.RedstoneIndicatorsLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MultiblockControllerMachine.class, remap = false)
public class MultiblockControllerMachineMixin implements IMultiblockControllerMachineRedstoneIndicators {

    @Persisted(key = "redstoneIndicators")
    @DescSynced
    @Unique
    private RedstoneIndicatorsLogic start_core$redstoneIndicatorsLogic;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        if (this instanceof IRedstoneIndicatorMachine machine) {
            start_core$redstoneIndicatorsLogic = new RedstoneIndicatorsLogic(machine);
        }
    }

    @Inject(method = "onStructureFormed", at = @At("TAIL"))
    private void injectOnStructureFormed(CallbackInfo ci) {
        if (start_core$redstoneIndicatorsLogic != null) {
            start_core$redstoneIndicatorsLogic.onStructureFormed();
        }
    }

    @Inject(method = "onStructureInvalid", at = @At("TAIL"))
    private void injectOnStructureInvalid(CallbackInfo ci) {
        if (start_core$redstoneIndicatorsLogic != null) {
            start_core$redstoneIndicatorsLogic.onStructureInvalid();
        }
    }

    @Override
    public RedstoneIndicatorsLogic start_core$getRedstoneIndicatorsTrait() {
        return start_core$redstoneIndicatorsLogic;
    }
}