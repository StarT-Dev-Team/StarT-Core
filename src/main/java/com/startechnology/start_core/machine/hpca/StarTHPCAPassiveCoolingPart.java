package com.startechnology.start_core.machine.hpca;

import com.gregtechceu.gtceu.api.capability.IHPCACoolantProvider;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.multiblock.part.hpca.HPCAComponentPartMachine;
import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;

public class StarTHPCAPassiveCoolingPart extends HPCAComponentPartMachine implements IHPCACoolantProvider {

    private final SpriteTexture componentIcon;
    private final int upkeepEUt;
    private final int coolingAmount;

    public StarTHPCAPassiveCoolingPart(IMachineBlockEntity holder, SpriteTexture componentIcon, int upkeepEUt,
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
    public SpriteTexture getComponentIcon() {
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
