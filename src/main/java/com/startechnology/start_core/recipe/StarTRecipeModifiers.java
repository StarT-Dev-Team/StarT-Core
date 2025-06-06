package com.startechnology.start_core.recipe;

import java.util.Optional;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IParallelHatch;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.llamalad7.mixinextras.lib.apache.commons.ObjectUtils.Null;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachine;
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

    public static final RecipeModifier HELL_FORGE_OC = StarTRecipeModifiers::hellforgeOverclock;

    public static ModifierFunction hellforgeOverclock(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof StarTHellForgeMachine coilMachine)) {
            return RecipeModifier.nullWrongType(StarTHellForgeMachine.class, machine);
        }

        int hellforgeTemp = coilMachine.getCrucibleTemperature();

        int recipeTemp = recipe.data.getInt("ebf_temp");
        if (!recipe.data.contains("ebf_temp")) {
            return ModifierFunction.IDENTITY;
        }

        if (recipeTemp > hellforgeTemp) {
            return ModifierFunction.NULL;
        }

        if (RecipeHelper.getRecipeEUtTier(recipe) > coilMachine.getTier()) {
            return ModifierFunction.NULL;
        }

        var diffOverAmount =  Math.pow(4, Math.floor(Math.max(0, (hellforgeTemp - recipeTemp) / 900)));

        var discount = ModifierFunction.builder()
                .parallels((int)diffOverAmount)
                .build();

        return discount;
    }
}
