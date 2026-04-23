package com.startechnology.start_core.mixin;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.startechnology.start_core.machine.parallel.IStarTMinimumParallelHatch;
import com.startechnology.start_core.machine.parallel.StarTAbsoluteParallelHatchMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value=GTRecipeModifiers.class, remap=false)
public class GTRecipeModifiersMixin {

    @Inject(method = "hatchParallel", at = @At("HEAD"), cancellable = true)
    private static void injectHatchParallel(MetaMachine machine, GTRecipe recipe, CallbackInfoReturnable<ModifierFunction> cir) {
        if (machine instanceof IMultiController controller && controller.isFormed()) {
            var hatch = controller.getParallelHatch().orElse(null);
            var maximumParallels = hatch instanceof StarTAbsoluteParallelHatchMachine ?
                    ParallelLogic.getParallelAmountWithoutEU(machine, recipe, hatch.getCurrentParallel()) :
                    hatch != null ? ParallelLogic.getParallelAmount(machine, recipe, hatch.getCurrentParallel()) : 1;
            var minimumParallels = hatch instanceof IStarTMinimumParallelHatch minHatch ? minHatch.getMinimumParallels() : 1;

            if (maximumParallels < minimumParallels) cir.setReturnValue(ModifierFunction.NULL);
            if (maximumParallels == 1) cir.setReturnValue(ModifierFunction.IDENTITY);

            // actually... I think I could make it better still

            cir.setReturnValue(ModifierFunction.builder()
                    .modifyAllContents(ContentModifier.multiplier(maximumParallels))
                    .eutMultiplier(maximumParallels)
                    .parallels(maximumParallels)
                    .build());
        }
    }
}
