package com.startechnology.start_core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.gregtechceu.gtceu.common.machine.multiblock.part.ParallelHatchPartMachine;

@Mixin(ParallelHatchPartMachine.class)
public interface ParallelHatchPartMachineAccessor {
    @Accessor("maxParallel")
    void start_core$maxParallel(int maxParallel);
}
