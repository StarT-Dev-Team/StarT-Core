package com.startechnology.start_core.machine.solar;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StarTSolarMachine extends WorkableElectricMultiblockMachine {

    private final long BASE_EU_OUTPUT;
    @Getter
    private final int tier;

    public StarTSolarMachine(IMachineBlockEntity holder, int tier) {
        super(holder);

        this.tier = tier;
        this.BASE_EU_OUTPUT = GTValues.V[tier] * 2;
    }

    public boolean regressWhenWaiting() {
        return false;
    }

    public boolean canVoidRecipeOutputs(RecipeCapability<?> capability) {
        return capability != EURecipeCapability.CAP;
    }
}
