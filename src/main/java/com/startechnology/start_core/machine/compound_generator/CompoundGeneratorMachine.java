package com.startechnology.start_core.machine.compound_generator;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.data.GTParallelTypes;
import com.gregtechceu.gtceu.common.machine.multiblock.part.EnergyHatchPartMachine;
import com.startechnology.start_core.recipe.StarTParallelTypes;
import lombok.Getter;

public class CompoundGeneratorMachine extends WorkableElectricMultiblockMachine {

    @Getter
    private final int tier;

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

    @Override
    public long getOverclockVoltage() {
        return GTValues.VEX[tier] * 2 * slices;
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof CompoundGeneratorMachine generator) || !generator.isFormed())
            return RecipeModifier.nullWrongType(CompoundGeneratorMachine.class, machine);

        var EUt = recipe.getOutputEUt().getTotalEU();
        if (EUt <= 0L)
            return ModifierFunction.NULL;

        var maxParallels = (int) (generator.getOverclockVoltage() / EUt);
        var parallels = ParallelLogic.getParallelAmount(machine, recipe, maxParallels);

        if (parallels == 0) return ModifierFunction.NULL;

        var maxTierParallels = (int) (GTValues.VEX[generator.tier] / EUt) * 2;
        var tierParallels = Math.min(parallels, maxTierParallels);
        var sliceParallels = parallels / tierParallels;
        var modifier = ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(parallels))
                .eutMultiplier(parallels);

        if (tierParallels > 1)
            modifier.parallels(tierParallels, GTParallelTypes.GENERATOR);

        if (sliceParallels > 1)
            modifier.parallels(sliceParallels, StarTParallelTypes.COMPOUND_GENERATOR);

        return modifier.build();
    }
}
