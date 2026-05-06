package com.startechnology.start_core.mixin;

import com.gregtechceu.gtceu.common.machine.multiblock.part.ParallelHatchPartMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ParallelHatchPartMachine.class, remap = false)
public interface ParallelHatchPartMachineAccessor {
    @Accessor("maxParallel")
    void start_core$maxParallel(int maxParallel);
}
