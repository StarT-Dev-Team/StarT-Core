package com.startechnology.start_core.recipe;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.startechnology.start_core.machine.parallel.IStarTAbsoluteParallelHatch;

public class StarTRecipeModifiers {
    public static final RecipeModifier ABSOLUTE_PARALLEL = StarTRecipeModifiers::hatchAbsoluteParallel;

    public static ModifierFunction hatchAbsoluteParallel(MetaMachine machine, GTRecipe recipe) {
        if (machine instanceof IMultiController controller && controller.isFormed()) {
            int parallels = controller.getParallelHatch()
                .filter(hatch -> hatch instanceof IStarTAbsoluteParallelHatch)
                .map(hatch -> ParallelLogic.getParallelAmount(machine, recipe, hatch.getCurrentParallel()))
                .orElse(1);
                    
            if (parallels == 1) return ModifierFunction.IDENTITY;

            return ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(parallels))
                .parallels(parallels)
                .build();
        }
        return ModifierFunction.IDENTITY;
    }

    public static final RecipeModifier EBF_OVERCLOCK = GTRecipeModifiers::ebfOverclock;
}
