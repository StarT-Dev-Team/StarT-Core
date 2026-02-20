package com.startechnology.start_core.recipe.recipes;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

public class SolarEnergyRecipes {
    public static void init(Consumer<FinishedRecipe> provider) {
        solarEnergyRecipes(provider);
    }

    public static void solarEnergyRecipes(Consumer<FinishedRecipe> provider) {
        GTRecipeType SOLAR_ENERGY_RECIPE_TYPE = GTRecipeTypes.get("solar_energy");

        SOLAR_ENERGY_RECIPE_TYPE.recipeBuilder("solar_energy")
                .EUt(-GTValues.V[GTValues.EV])
                .save(provider);
    }
}
