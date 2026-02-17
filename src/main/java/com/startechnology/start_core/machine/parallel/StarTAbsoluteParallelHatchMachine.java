package com.startechnology.start_core.machine.parallel;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ParallelHatchPartMachine;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.mixin.ParallelHatchPartMachineAccessor;

public class StarTAbsoluteParallelHatchMachine extends ParallelHatchPartMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            StarTAbsoluteParallelHatchMachine.class, MultiblockPartMachine.MANAGED_FIELD_HOLDER);

    public StarTAbsoluteParallelHatchMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
        Integer absoluteMaxParallels = 4 * (int) Math.pow(2, tier - GTValues.UHV);

        // UHV -> 4, UEV -> 8, UIV -> 16
        ((ParallelHatchPartMachineAccessor)(Object)this).start_core$maxParallel(absoluteMaxParallels);
        this.setCurrentParallel(absoluteMaxParallels);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}   
