package com.startechnology.start_core.recipe.recipes;

import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.startechnology.start_core.item.StarTItems;
import com.startechnology.start_core.machine.black_hole.BlackHoleSeeds;
import com.startechnology.start_core.machine.dyson_sphere.StellarBalance;

import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.common.data.GTMaterials.Helium;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Hydrogen;
import static com.startechnology.start_core.recipe.StarTRecipeTypes.DYSON_SPHERE_FUEL_RECIPES;
import static com.startechnology.start_core.recipe.StarTRecipeTypes.DYSON_SPHERE_REMNANT_RECIPES;

public class DysonSphereRecipes {

    public static final String RECIPE_DATA_STAGE = "stage";
    public static final String RECIPE_DATA_MASS = "mass";
    public static final String RECIPE_DATA_OUTCOME = "outcome";
    private static final String HIDE_DURATION = "hide_duration";

    public static void init(Consumer<FinishedRecipe> provider) {
        DYSON_SPHERE_FUEL_RECIPES.recipeBuilder("hydrogen")
                .inputFluids(Hydrogen.getFluid(1000))
                .addData(RECIPE_DATA_STAGE, "main_sequence")
                .addData(RECIPE_DATA_MASS, 1000f / StellarBalance.MB_PER_MASS)
                .addData(HIDE_DURATION, true)
                .save(provider);
        DYSON_SPHERE_FUEL_RECIPES.recipeBuilder("helium_plasma")
                .inputFluids(Helium.getFluid(FluidStorageKeys.PLASMA, 1000))
                .addData(RECIPE_DATA_STAGE, "red_giant")
                .addData(HIDE_DURATION, true)
                .save(provider);

        DYSON_SPHERE_REMNANT_RECIPES.recipeBuilder("nebula")
                .addData(RECIPE_DATA_OUTCOME, "nebula")
                .addData(HIDE_DURATION, true)
                .save(provider);
        DYSON_SPHERE_REMNANT_RECIPES.recipeBuilder("neutron_star")
                .outputItems(StarTItems.NEUTRON_STAR_REMNANT.asStack())
                .addData(RECIPE_DATA_OUTCOME, "neutron_star")
                .addData(HIDE_DURATION, true)
                .save(provider);
        DYSON_SPHERE_REMNANT_RECIPES.recipeBuilder("singularity_seed")
                .outputItems(StarTItems.SINGULARITY_SEEDS.get(BlackHoleSeeds.STELLAR_REMNANT.id()).asStack())
                .addData(RECIPE_DATA_OUTCOME, "seed")
                .addData(HIDE_DURATION, true)
                .save(provider);
    }
}
