package com.startechnology.start_core.machine.compound_generator;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.common.machine.multiblock.part.EnergyHatchPartMachine;
import com.startechnology.start_core.recipe.StarTParallelTypes;
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
        slices = (int) getParts().stream().filter(EnergyHatchPartMachine.class::isInstance).count();
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (machine instanceof CompoundGeneratorMachine controller && controller.isFormed()) {
            int tier = controller.getTier(); // Voltage tier (1, 2 or 3)
            int slices = controller.getSlices(); // 1 slice = 2 parallels
            int maxParallels = slices * 2 * (int) Math.pow(4, tier - 1);
            int parallelsAvailable = ParallelLogic.getParallelAmountWithoutEU(machine, recipe, maxParallels);
            if (parallelsAvailable > 0) {
                int parallels = Math.min(parallelsAvailable, maxParallels);
                return ModifierFunction.builder()
                        .parallels(parallels, StarTParallelTypes.COMPOUND_GENERATOR)
                        .eutMultiplier(parallels) // 32 * parallels
                        .build();
            }
        }
        return ModifierFunction.IDENTITY;
    }
}
