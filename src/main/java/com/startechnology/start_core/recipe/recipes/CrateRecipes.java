package com.startechnology.start_core.recipe.recipes;

import com.gregtechceu.gtceu.api.data.chemical.material.stack.UnificationEntry;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.plate;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.rodLong;
import static com.gregtechceu.gtceu.common.data.GTMaterials.NaquadahEnriched;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Neutronium;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.ASSEMBLER_RECIPES;
import static com.startechnology.start_core.machine.crates.StarTCrates.ENRICHED_NAQUADAH_CRATE;
import static com.startechnology.start_core.machine.crates.StarTCrates.NEUTRONIUM_CRATE;

public class CrateRecipes {
    public static final void init(Consumer<FinishedRecipe> provider) {
        customDrumRecipes(provider);
    }

    public static void customDrumRecipes(Consumer<FinishedRecipe> provider) {

        VanillaRecipeHelper.addShapedRecipe(provider, true, "enriched_naquadah_crate", ENRICHED_NAQUADAH_CRATE.asStack(),
                "RPR",
                "PhP",
                "RPR",
                'P', new UnificationEntry(plate, NaquadahEnriched),
                'R', new UnificationEntry(rodLong, NaquadahEnriched));

        ASSEMBLER_RECIPES.recipeBuilder("enriched_naquadah_crate")
                .inputItems(rodLong, NaquadahEnriched, 4)
                .inputItems(plate, NaquadahEnriched, 4)
                .outputItems(ENRICHED_NAQUADAH_CRATE)
                .duration(200)
                .EUt(16)
                .circuitMeta(1)
                .save(provider);

        VanillaRecipeHelper.addShapedRecipe(provider, true, "neutronium_crate", NEUTRONIUM_CRATE.asStack(),
                "RPR",
                "PhP",
                "RPR",
                'P', new UnificationEntry(plate, Neutronium),
                'R', new UnificationEntry(rodLong, Neutronium));

        ASSEMBLER_RECIPES.recipeBuilder("neutronium_crate")
                .inputItems(rodLong, Neutronium, 4)
                .inputItems(plate, Neutronium, 4)
                .outputItems(NEUTRONIUM_CRATE)
                .duration(200)
                .EUt(16)
                .circuitMeta(1)
                .save(provider);
    }
}
