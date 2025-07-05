package com.startechnology.start_core.recipe.recipes;

import java.util.Map;
import java.util.function.Consumer;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.startechnology.start_core.materials.StarTAbyssalHarvesterVoidFluids;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import static com.startechnology.start_core.recipe.StarTRecipeTypes.ABYSSAL_HARVESTER_RECIPES;

import net.minecraft.data.recipes.FinishedRecipe;



public class AbyssalHarvesterRecipes {

    public static final void init(Consumer<FinishedRecipe> provider) {
        abyssalHarvesting(provider);
    }
    

    public static void abyssalHarvesting(Consumer<FinishedRecipe> provider) {
        // Map<Material, Integer> entropyVoidMap = Map.of(
        //     StarTAbyssalHarvesterVoidFluids.CorruptAbyssalVoid, 1000,
        //     StarTAbyssalHarvesterVoidFluids.TaintedAbyssalVoid, 2000,
        //     StarTAbyssalHarvesterVoidFluids.PollutedAbyssalVoid, 3000,
        //     StarTAbyssalHarvesterVoidFluids.MuddiedAbyssalVoid, 4000,
        //     StarTAbyssalHarvesterVoidFluids.DillutedAbyssalVoid, 5000,
        //     StarTAbyssalHarvesterVoidFluids.FilteredAbyssalVoid, 6000,
        //     StarTAbyssalHarvesterVoidFluids.RefinedAbyssalVoid, 7000,
        //     StarTAbyssalHarvesterVoidFluids.ClarifiedAbyssalVoid, 8000,
        //     StarTAbyssalHarvesterVoidFluids.PurifiedAbyssalVoid, 9000,
        //     StarTAbyssalHarvesterVoidFluids.PristineAbyssalVoid, 10000,
        //     StarTAbyssalHarvesterVoidFluids.ImmaculateAbyssalVoid, 11000,
        //     StarTAbyssalHarvesterVoidFluids.AbsoluteAbyssalVoid, 12000
        // ); 

        // ABYSSAL_HARVESTER_RECIPES
        //     .recipeBuilder("abyssal_harvester_void")
        //     .outputFluids(StarTAbyssalHarvesterVoidFluids.CorruptAbyssalVoid.getFluid(1000))
        //     .duration(100)
        //     .addData("entropy", 1000)
        //     .save(provider);
    }
}
