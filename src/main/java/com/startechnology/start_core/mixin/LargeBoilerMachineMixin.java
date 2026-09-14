package com.startechnology.start_core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.machine.multiblock.steam.LargeBoilerMachine;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.startechnology.start_core.materials.StarTSteams;

import net.minecraft.world.level.material.Fluid;

@Mixin(value = LargeBoilerMachine.class, remap = false)
public abstract class LargeBoilerMachineMixin {

    @Shadow
    public abstract int getMaxTemperature();

    private final Fluid steam = GTMaterials.Steam.getFluid();

    /**
     * @author trulyno
     * @reason Large Boilers rework
     */
    @Overwrite
    public Fluid getProducedFluid() {
        if (this.getMaxTemperature() > ConfigHolder.INSTANCE.machines.largeBoilers.titaniumBoilerMaxTemperature) {
            return StarTSteams.ExtremelyHotSteam.getFluid();
        } else if (this.getMaxTemperature() > ConfigHolder.INSTANCE.machines.largeBoilers.steelBoilerMaxTemperature) {
            return StarTSteams.HotSteam.getFluid();
        } else if (this.getMaxTemperature() > ConfigHolder.INSTANCE.machines.largeBoilers.bronzeBoilerMaxTemperature) {
            return StarTSteams.WarmSteam.getFluid();
        }

        return steam;
    }
}
