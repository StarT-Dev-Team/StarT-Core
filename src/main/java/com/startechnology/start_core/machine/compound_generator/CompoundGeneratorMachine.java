package com.startechnology.start_core.machine.compound_generator;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.startechnology.start_core.utils.StarTMath;

import lombok.Getter;

public class CompoundGeneratorMachine extends WorkableElectricMultiblockMachine {

    @Getter
    private int tier;

    @Getter
    private int slices;

    public CompoundGeneratorMachine(IMachineBlockEntity holder, int tier, Object... args) {
        super(holder, args);
        this.tier = tier;
        this.slices = 0;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        BlockPattern pattern = this.getPattern();
        int[] dimensions = pattern.getDimensions();
        if (pattern != null) {
            this.slices = StarTMath.max(dimensions) - 2;
        }
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (machine instanceof CompoundGeneratorMachine controller && controller.isFormed()) {
            int tier = controller.getTier(); // Voltage tier (0, 1, 2)
            int slices = controller.getSlices(); // 1 slice = 2 parallels
            int parallels = ParallelLogic.getParallelAmountWithoutEU(machine, recipe, slices * 2);
            if (parallels > 0) {
                return ModifierFunction.builder()
                    .parallels(parallels)
                    .eutMultiplier(parallels * Math.pow(4, tier))
                    .build();
            }
        }
        return ModifierFunction.IDENTITY;
    }
}
