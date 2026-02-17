package com.startechnology.start_core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.gregtechceu.gtceu.api.capability.IParallelHatch;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ParallelHatchPartMachine;
import com.startechnology.start_core.machine.parallel.IStarTMinimumParallelHatch;
import com.startechnology.start_core.machine.parallel.StarTAbsoluteParallelHatchMachine;

@Mixin(value=GTRecipeModifiers.class, remap=false)
public class GTRecipeModifiersMixin {

    /* is this a crime against good java programming? perhaps... does it work? yes. */
    @Overwrite
    public static ModifierFunction hatchParallel(MetaMachine machine, GTRecipe recipe) {
        if (machine instanceof IMultiController controller && controller.isFormed()) {
            int parallels = controller.getParallelHatch()
                    .map(hatch -> {
                        int maximumParallels = 1;
                        int minimumParallels = 1;

                        /* the great crime !!! look away */
                        if (hatch instanceof StarTAbsoluteParallelHatchMachine) {
                            maximumParallels = ParallelLogic.getParallelAmountWithoutEU(machine, recipe, hatch.getCurrentParallel());
                        } else {
                            maximumParallels = ParallelLogic.getParallelAmount(machine, recipe, hatch.getCurrentParallel());
                        }

                        if (hatch instanceof IStarTMinimumParallelHatch minHatch) {
                            minimumParallels = minHatch.getMinimumParallels();
                        }

                        if (maximumParallels >= minimumParallels) {
                            return maximumParallels;
                        }

                        return 0;
                    })
                    .orElse(1);

            if (parallels == 0) return ModifierFunction.NULL;
            if (parallels == 1) return ModifierFunction.IDENTITY;
            return ModifierFunction.builder()
                    .modifyAllContents(ContentModifier.multiplier(parallels))
                    .eutMultiplier(parallels)
                    .parallels(parallels)
                    .build();
        }
        return ModifierFunction.IDENTITY;
    }
}
