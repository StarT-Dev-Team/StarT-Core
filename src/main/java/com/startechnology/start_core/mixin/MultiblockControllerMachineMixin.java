package com.startechnology.start_core.mixin;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.startechnology.start_core.machine.redstone.IMultiblockControllerMachineRedstoneIndicators;
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
        start_core$redstoneIndicatorsLogic = new RedstoneIndicatorsLogic((IMultiController)this);
    }

    @Inject(method = "onStructureFormed", at = @At("TAIL"))
    private void injectOnStructureFormed(CallbackInfo ci) {
        start_core$redstoneIndicatorsLogic.onStructureFormed();
    }

    @Inject(method = "onStructureInvalid", at = @At("TAIL"))
    private void injectOnStructureInvalid(CallbackInfo ci) {
        start_core$redstoneIndicatorsLogic.onStructureInvalid();
    }

    @Override
    public RedstoneIndicatorsLogic start_core$getRedstoneIndicatorsTrait() {
        return start_core$redstoneIndicatorsLogic;
    }
}