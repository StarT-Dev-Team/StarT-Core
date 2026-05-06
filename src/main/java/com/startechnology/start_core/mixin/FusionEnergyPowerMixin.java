package com.startechnology.start_core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FusionReactorMachine;

@Mixin(value=FusionReactorMachine.class, remap=false)
public class FusionEnergyPowerMixin {
    /**
     * @author stella
     * @reason changing the energy storage factor
     */
    @Overwrite
    public static long calculateEnergyStorageFactor(int tier, int energyInputAmount) {
        long energyFactor = switch (tier - GTValues.LuV) {
            case 0 -> 1;
            default -> 2L * (tier - GTValues.LuV);
        };
        
        return energyInputAmount * energyFactor * 10000000L;
    }
}
