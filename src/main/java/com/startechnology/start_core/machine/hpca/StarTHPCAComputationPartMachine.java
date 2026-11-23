package com.startechnology.start_core.machine.hpca;

import com.gregtechceu.gtceu.api.capability.IHPCAComputationProvider;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.multiblock.part.hpca.HPCAComponentPartMachine;
import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class StarTHPCAComputationPartMachine extends HPCAComponentPartMachine implements IHPCAComputationProvider {

    private final SpriteTexture componentIcon;
    private final SpriteTexture brokenComponentIcon;
    private final int upkeepEUt;
    private final int maxEUt;
    private final int cwuT;
    private final int coolingT;

    @Getter
    private final boolean advanced;

    public StarTHPCAComputationPartMachine(IMachineBlockEntity holder, SpriteTexture componentIcon,
                                           SpriteTexture brokenComponentIcon, int upkeepEUt, int maxEUt, int cwuT,
                                           int coolingT) {
        super(holder);

        this.componentIcon = componentIcon;
        this.brokenComponentIcon = brokenComponentIcon;
        this.upkeepEUt = upkeepEUt;
        this.maxEUt = maxEUt;
        this.cwuT = cwuT;
        this.coolingT = coolingT;

        this.advanced = true;
    }

    @Override
    public SpriteTexture getComponentIcon() {
        if (isDamaged()) {
            return brokenComponentIcon;
        } else {
            return componentIcon;
        }
    }

    @Override
    public int getUpkeepEUt() {
        return upkeepEUt;
    }

    @Override
    public int getMaxEUt() {
        return maxEUt;
    }

    @Override
    public int getCWUPerTick() {
        if (isDamaged()) return 0;

        return cwuT;
    }

    @Override
    public int getCoolingPerTick() {
        return coolingT;
    }

    @Override
    public boolean canBeDamaged() {
        return true;
    }
}
