package com.startechnology.start_core.machine.redstone;

import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachine;

import net.minecraft.core.Direction;

public class StarTRedstoneInterfacePartMachine extends TieredIOPartMachine {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(StarTRedstoneInterfacePartMachine.class,
        TieredIOPartMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    protected int currentSignal;

    public int getCurrentSignal() {
        return currentSignal;
    }

    public void setCurrentSignal(int currentSignal) {
        if (currentSignal == this.currentSignal) return;
        this.currentSignal = currentSignal;
        notifyBlockUpdate();
    }

    public StarTRedstoneInterfacePartMachine(IMachineBlockEntity holder, int tier, IO io) {
        super(holder, tier, io);
        this.currentSignal = 0;
    }
    
    @Override
    public int getOutputSignal(@Nullable Direction side) {
        if (side.getOpposite() != this.getFrontFacing()) return 0;
        return this.currentSignal;
    }

    @Override
    public boolean canConnectRedstone(Direction side) {
        if (side == this.getFrontFacing()) return true;
        return false;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}
