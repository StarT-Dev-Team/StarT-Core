package com.startechnology.start_core.machine.hpca;

import com.gregtechceu.gtceu.api.capability.IHPCACoolantProvider;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.multiblock.part.hpca.HPCAComponentPartMachine;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;

public class StarTHPCAPassiveCoolingPart extends HPCAComponentPartMachine implements IHPCACoolantProvider {

    private final ResourceTexture componentIcon;
    private final int upkeepEUt;
    private final int coolingAmount;

    public StarTHPCAPassiveCoolingPart(IMachineBlockEntity holder, ResourceTexture componentIcon, int upkeepEUt,
                                       int coolingAmount) {
        super(holder);
        this.componentIcon = componentIcon;
        this.upkeepEUt = upkeepEUt;
        this.coolingAmount = coolingAmount;
    }

    @Override
    public int getUpkeepEUt() {
        return upkeepEUt;
    }

    @Override
    public boolean canBeDamaged() {
        return false;
    }

    @Override
    public ResourceTexture getComponentIcon() {
        return componentIcon;
    }

    @Override
    public int getCoolingAmount() {
        return coolingAmount;
    }

    @Override
    public boolean isActiveCooler() {
        return false;
    }

    @Override
    public boolean isAdvanced() {
        return true;
    }
}
