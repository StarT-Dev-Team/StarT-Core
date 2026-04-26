package com.startechnology.start_core.recipe;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.IdentifiedRecipeModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.machine.multiblock.generator.LargeTurbineMachine;
import com.startechnology.start_core.machine.boosting.BoostedPlasmaTurbine;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachine;
import com.startechnology.start_core.machine.steam.StarTSteamParallelMultiblockMachine;
import com.startechnology.start_core.machine.threading.StarTThreadingCapableMachine;
import com.startechnology.start_core.machine.vcrc.VacuumChemicalReactionChamberMachine;

public class StarTRecipeModifiers {
    public static final RecipeModifier ABSOLUTE_PARALLEL = new IdentifiedRecipeModifier("absolute_parallel", GTRecipeModifiers::hatchParallel);

    public static final RecipeModifier HELL_FORGE_OC = StarTRecipeModifiers::hellforgeOverclock;

    public static ModifierFunction hellforgeOverclock(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof StarTHellForgeMachine coilMachine)) {
            return RecipeModifier.nullWrongType(StarTHellForgeMachine.class, machine);
        }

        int hellforgeTemp = coilMachine.getTemperature();

        int recipeTemp = recipe.data.getInt("ebf_temp");

        if (!recipe.data.contains("ebf_temp")) {
            return ModifierFunction.IDENTITY;
        }
        
        if (recipeTemp > hellforgeTemp) {
            return ModifierFunction.NULL;
        }

        double timesScaled = Math.floor(Math.max(0.0, (hellforgeTemp - recipeTemp) / 450.0));
        int hellforgeParallels = (int) Math.pow(2.0, timesScaled);
        
        int maxPossibleParallels = ParallelLogic.getParallelAmountWithoutEU(machine, recipe, hellforgeParallels);

        // Runs largest 2^n parallels that it can. 1,2,4,8,16,etc.
        return ModifierFunction.builder()
            .modifyAllContents(ContentModifier.multiplier(maxPossibleParallels))
            .parallels(maxPossibleParallels)
            .build();
        }

    public static final RecipeModifier BULK_PROCESSING = new IdentifiedRecipeModifier("bulk_processing", StarTRecipeModifiers::bulkThroughputProcessing);

    public static ModifierFunction bulkThroughputProcessing(MetaMachine machine, GTRecipe recipe) {
        int throughputModifier = 16;
        int durationModifier = 13;

        var parallelsAvailable = Math.max(0, ParallelLogic.getParallelAmountWithoutEU(machine, recipe, throughputModifier));

        if (parallelsAvailable >= throughputModifier) {

            return ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(throughputModifier))
                .durationMultiplier(durationModifier)
                .parallels(throughputModifier)
                .build();
        }
        
        return ModifierFunction.IDENTITY;
  
    }

    public static final RecipeModifier THROUGHPUT_BOOSTING = new IdentifiedRecipeModifier("throughput_boosting", StarTRecipeModifiers::throughputBoosting);

    public static ModifierFunction throughputBoosting(MetaMachine machine, GTRecipe recipe) {
        int throughputModifier = 4;
        double durationModifier = 1.6;
        double eutModifier = 0.95;

        int parallelsAvailable = Math.max(0, ParallelLogic.getParallelAmountWithoutEU(machine, recipe, throughputModifier));

        if (parallelsAvailable >= throughputModifier) {

            return ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(throughputModifier)) 
                .durationMultiplier(durationModifier)
                .eutMultiplier(eutModifier)
                .parallels(throughputModifier)    
                .build();

        }
        
        return ModifierFunction.IDENTITY;
  
    }

    public static final RecipeModifier LARGE_TURBINE = LargeTurbineMachine::recipeModifier;

    public static final RecipeModifier BOOSTED_PLASMA_TURBINE = BoostedPlasmaTurbine::recipeModifier;

    public static final RecipeModifier THREADING_MACHINE = StarTThreadingCapableMachine::recipeModifier;

    public static final RecipeModifier START_STEAM_PARALLEL = StarTSteamParallelMultiblockMachine::recipeModifier;

    public static final RecipeModifier VACUUM_CHEMICAL_REACTION_CHAMBER = VacuumChemicalReactionChamberMachine::recipeModifier;
}
