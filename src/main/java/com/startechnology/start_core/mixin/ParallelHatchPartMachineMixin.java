package com.startechnology.start_core.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ParallelHatchPartMachine;

@Mixin(value = ParallelHatchPartMachine.class, remap = false)
public class ParallelHatchPartMachineMixin {

    @Mutable
    @Final
    @Shadow
    private int maxParallel;

    @Mutable
    @Shadow
    private int currentParallel;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void modifyMaxParallel(IMachineBlockEntity holder, int tier, CallbackInfo ci) {
        // Change the maxParallel calculation from Math.pow(4, tier - GTValues.EV)
        // to a custom expression: 2 * (int) Math.pow(4, tier - GTValues.EV)
        // This doubles the parallel capacity compared to the original
        this.maxParallel = (tier <= GTValues.UHV) ? (int) Math.pow(4, tier - GTValues.EV) : 
                           (int) Math.pow(2, tier + 1);
        this.currentParallel = maxParallel;
    }
}
