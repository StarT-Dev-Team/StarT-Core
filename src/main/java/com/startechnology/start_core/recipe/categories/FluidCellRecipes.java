package com.startechnology.start_core.recipe.categories;

import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.startechnology.start_core.item.StarTItems.FLUID_CELL_LARGE_ENRICHED_NAQUADAH;
import static com.startechnology.start_core.item.StarTItems.FLUID_CELL_LARGE_NEUTRONIUM;

public class FluidCellRecipes {

    public static final void init(Consumer<FinishedRecipe> provider) {
        customDrumRecipes(provider);
    }

    public static void customDrumRecipes(Consumer<FinishedRecipe> provider) {

        ASSEMBLER_RECIPES.recipeBuilder("fluid_cell_large_enriched_naquadah")
                .inputItems(plateDouble, NaquadahEnriched, 6)
                .inputItems(ring, Osmiridium, 6)
                .outputItems(FLUID_CELL_LARGE_ENRICHED_NAQUADAH)
                .duration(200)
                .EUt(1024)
                .save(provider);

        ASSEMBLER_RECIPES.recipeBuilder("fluid_cell_large_neutronium")
                .inputItems(plateDouble, Neutronium, 8)
                .inputItems(ring, NaquadahAlloy, 8)
                .outputItems(FLUID_CELL_LARGE_NEUTRONIUM)
                .duration(200)
                .EUt(VA[EV])
                .save(provider);
    }

}
