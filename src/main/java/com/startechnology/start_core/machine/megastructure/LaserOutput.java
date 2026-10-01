package com.startechnology.start_core.machine.megastructure;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableLaserContainer;
import com.gregtechceu.gtceu.common.machine.multiblock.part.LaserHatchPartMachine;
import com.startechnology.start_core.mixin.LaserHatchPartMachineAccessor;

import java.util.ArrayList;
import java.util.List;

public final class LaserOutput {

    private final List<NotifiableLaserContainer> containers = new ArrayList<>();

    public void collect(List<IMultiPart> parts) {
        containers.clear();
        for (var part : parts) {
            if (part instanceof LaserHatchPartMachine laserHatch) {
                var container = ((LaserHatchPartMachineAccessor) laserHatch).start_core$getLaserContainer();
                if (container.getHandlerIO() == IO.OUT) containers.add(container);
            }
        }
    }

    public void clear() {
        containers.clear();
    }

    public long emit(long amount) {
        long remaining = amount;
        for (var container : containers) {
            if (remaining <= 0) break;
            long added = Math.min(container.getEnergyCapacity() - container.getEnergyStored(), remaining);
            if (added > 0) {
                container.addEnergy(added);
                remaining -= added;
            }
        }
        return amount - remaining;
    }
}
