package com.startechnology.start_core.recipe.recipes.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.startechnology.start_core.item.StarTItems;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plate;
import static com.gregtechceu.gtceu.common.data.GTMaterials.TantalumCarbide;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.startechnology.start_core.item.gcrops.StarTGCropItems.EMPTY_GENOME_HOLDER;

public class GenomeHolderRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        holderRecipe(provider);
    }

    public static void holderRecipe(Consumer<FinishedRecipe> provider) {
        ASSEMBLER_RECIPES.recipeBuilder("empty_genome_holder")
                .inputItems(StarTItems.FLUID_CELL_LARGE_NEUTRONIUM.asStack())
                .inputItems(GTItems.FIELD_GENERATOR_UHV.asStack())
                .inputItems(ChemicalHelper.get(plate, TantalumCarbide, 4))
                .outputItems(EMPTY_GENOME_HOLDER.asStack())
                .duration(200)
                .EUtVA(GTValues.UEV)
                .save(provider);
    }
}
