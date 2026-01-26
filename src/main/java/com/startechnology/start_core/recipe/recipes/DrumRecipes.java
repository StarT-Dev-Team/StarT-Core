package com.startechnology.start_core.recipe.recipes;

import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plate;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.rodLong;
import static com.gregtechceu.gtceu.common.data.GTMaterials.NaquadahEnriched;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Neutronium;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.startechnology.start_core.machine.drum.StarTDrumMachines.ENRICHED_NAQUADAH_DRUM;
import static com.startechnology.start_core.machine.drum.StarTDrumMachines.NEUTRONIUM_DRUM;

public class DrumRecipes {

    public static final void init(Consumer<FinishedRecipe> provider) {
        customDrumRecipes(provider);
    }

    public static void customDrumRecipes(Consumer<FinishedRecipe> provider) {

        VanillaRecipeHelper.addShapedRecipe(provider, true, "enriched_naquadah_drum", ENRICHED_NAQUADAH_DRUM.asStack(),
                " h ",
                "PRP",
                "PRP",
                'P', new MaterialEntry(plate, NaquadahEnriched),
                'R', new MaterialEntry(rodLong, NaquadahEnriched));

        ASSEMBLER_RECIPES.recipeBuilder("enriched_naquadah_drum")
                .inputItems(rodLong, NaquadahEnriched, 2)
                .inputItems(plate, NaquadahEnriched, 4)
                .outputItems(ENRICHED_NAQUADAH_DRUM)
                .duration(200)
                .EUt(16)
                .circuitMeta(2)
                .save(provider);

        VanillaRecipeHelper.addShapedRecipe(provider, true, "neutronium_drum", NEUTRONIUM_DRUM.asStack(),
                " h ",
                "PRP",
                "PRP",
                'P', new MaterialEntry(plate, Neutronium),
                'R', new MaterialEntry(rodLong, Neutronium));

        ASSEMBLER_RECIPES.recipeBuilder("neutronium_drum")
                .inputItems(rodLong, Neutronium, 2)
                .inputItems(plate, Neutronium, 4)
                .outputItems(NEUTRONIUM_DRUM)
                .duration(200)
                .EUt(16)
                .circuitMeta(2)
                .save(provider);
    }
}
