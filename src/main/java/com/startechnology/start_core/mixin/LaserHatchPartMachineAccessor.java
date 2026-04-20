package com.startechnology.start_core.mixin;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableLaserContainer;
import com.gregtechceu.gtceu.common.machine.multiblock.part.LaserHatchPartMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(value = LaserHatchPartMachine.class, remap = false)
public interface LaserHatchPartMachineAccessor {
    /* This is a getter for the energy container of the laser hatch, used for Komaru's scaling */
    @Accessor("buffer")
    NotifiableLaserContainer start_core$getLaserContainer();
}
