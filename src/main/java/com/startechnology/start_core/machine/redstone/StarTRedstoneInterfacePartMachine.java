package com.startechnology.start_core.machine.redstone;

import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;

import net.minecraft.core.Direction;

public class StarTRedstoneInterfacePartMachine extends TieredIOPartMachine {

    public StarTRedstoneInterfacePartMachine(IMachineBlockEntity holder, int tier, IO io) {
        super(holder, tier, io);
    }
    
    @Override
    public int getOutputSignal(@Nullable Direction side) {
        return super.getOutputSignal(side);
    }
}
