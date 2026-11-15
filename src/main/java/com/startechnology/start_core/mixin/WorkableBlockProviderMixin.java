package com.startechnology.start_core.mixin;

import com.gregtechceu.gtceu.api.capability.IWorkable;
import com.gregtechceu.gtceu.integration.jade.provider.WorkableBlockProvider;
import com.startechnology.start_core.machine.dimensional_finder.DimensionalFinderMachine;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorkableBlockProvider.class, remap = false)
public class WorkableBlockProviderMixin {

    @Inject(method = "write(Lnet/minecraft/nbt/CompoundTag;Lcom/gregtechceu/gtceu/api/capability/IWorkable;)V",
            at = @At("TAIL"))
    private void startCore$displayDurationForDimensionalFinder(CompoundTag data, IWorkable capData, CallbackInfo ci) {
        if (capData instanceof DimensionalFinderMachine) {
            data.remove("Research");
        }
    }
}
