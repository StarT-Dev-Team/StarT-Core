package com.startechnology.start_core.machine.redstone;

import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.core.Direction;

public class StarTRedstoneInterfacePartMachine extends TieredIOPartMachine {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(StarTRedstoneInterfacePartMachine.class,
        TieredIOPartMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    protected StarTRedstoneIndicatorMap indicatorMap;

    public StarTRedstoneInterfacePartMachine(IMachineBlockEntity holder, int tier, IO io) {
        super(holder, tier, io);
        this.indicatorMap = new StarTRedstoneIndicatorMap();
    }

    public StarTRedstoneIndicatorRecord getCurrentIndicator() {
        return this.indicatorMap.getCurrent();
    }

    public void setCurrentIndicator(String indicatorKey) {
        this.indicatorMap.setCurrent(indicatorKey);
    }

    public void putIndicator(StarTRedstoneIndicatorRecord indicator) {
        this.indicatorMap.put(indicator);
    }

    public void updateIndicator(String indicatorKey, Integer redstoneLevel) {
        this.indicatorMap.setRedstoneLevel(indicatorKey, redstoneLevel);
    }
    
    @Override
    public int getOutputSignal(@Nullable Direction side) {
        if (side.getOpposite() != this.getFrontFacing()) return 0;
        return this.indicatorMap.getCurrent().redstoneLevel();
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
