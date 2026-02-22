package com.startechnology.start_core.machine.solar;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCell;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StarTSolarMachine extends WorkableElectricMultiblockMachine {

    private final long EU_OUTPUT;
    @Getter
    private final int tier;
    private List<StarTSolarCell> panels;

    public StarTSolarMachine(IMachineBlockEntity holder, int tier) {
        super(holder);

        this.EU_OUTPUT = GTValues.VHA[tier];
        this.tier = tier;
        this.panels = new ArrayList<>();
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        this.getParts().forEach(part -> {
            if (part instanceof StarTSolarCell) {
                panels.add((StarTSolarCell) part);
            }
        });
    }

    public boolean regressWhenWaiting() {
        return false;
    }

    public boolean canVoidRecipeOutputs(RecipeCapability<?> capability) {
        return capability != EURecipeCapability.CAP;
    }
}
