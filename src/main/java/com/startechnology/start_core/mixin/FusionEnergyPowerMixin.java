package com.startechnology.start_core.mixin;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FusionReactorMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = FusionReactorMachine.class, remap = false)
public class FusionEnergyPowerMixin {

    /**
     * @author stellaurora
     * @reason change fusion reactor energy storage formula
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
