package com.startechnology.start_core.mixin;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.startechnology.start_core.machine.parallel.StarTAbsoluteParallelHatchMachine;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GTRecipeModifiers.class, remap = false)
public class GTRecipeModifiersMixin {

    @Inject(method = "hatchParallel", at = @At("HEAD"), cancellable = true)
    private static void injectHatchParallel(MetaMachine machine, GTRecipe recipe,
                                            CallbackInfoReturnable<ModifierFunction> cir) {
        if (!(machine instanceof IMultiController controller) || (!controller.isFormed())) return;

        var hatch = controller.getParallelHatch().orElse(null);
        if (!(hatch instanceof StarTAbsoluteParallelHatchMachine)) return;

        var maximumParallels = ParallelLogic.getParallelAmountWithoutEU(machine, recipe, hatch.getCurrentParallel());
        var minimumParallels = hatch.getMinimumParallel();

        if (maximumParallels < minimumParallels) {
            cir.setReturnValue(ModifierFunction
                    .cancel(Component.translatable("gtceu.recipe_modifier.cant_perform_at_min_parallel")));
        }

        if (maximumParallels == 1) {
            cir.setReturnValue(ModifierFunction.IDENTITY);
        }

        cir.setReturnValue(ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(maximumParallels))
                .parallels(maximumParallels)
                .build());
    }
}
