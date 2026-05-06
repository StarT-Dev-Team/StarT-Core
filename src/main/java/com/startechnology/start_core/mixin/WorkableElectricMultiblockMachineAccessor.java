package com.startechnology.start_core.mixin;

import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = WorkableElectricMultiblockMachine.class, remap = false)
public interface WorkableElectricMultiblockMachineAccessor {
    @Accessor("tier")
    void start_core$setTier(int tier);
}
