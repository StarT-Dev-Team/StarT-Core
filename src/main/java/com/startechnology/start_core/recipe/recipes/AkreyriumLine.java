package com.startechnology.start_core.recipe.recipes;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.chance.logic.ChanceLogic;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.startechnology.start_core.materials.StarTMaterials;

import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

public class AkreyriumLine {

    public static void init(Consumer<FinishedRecipe> provider) {
        leptonicManifoldQuantiserRecipes(provider);
    }

    public static void leptonicManifoldQuantiserRecipes(Consumer<FinishedRecipe> provider) {
        GTRecipeType MANIFOLD_CENTRIFUGE_RECIPE_TYPE = GTRecipeTypes.get("manifold_centrifuge");

        if (MANIFOLD_CENTRIFUGE_RECIPE_TYPE == null) {
            System.out.println(
                    "Invalid KubeJS Scripts detected, You are missing elements from the Star Technology modpack!");
            System.out.println("Star Technology Core may not work as expected without the full modpack.");
            return;
        }

        // KubeJS Interop, get materials registered.
        Material lepton_sparse_akreyrium = StarTMaterials.LeptonSparseAkreyrium;
        Material sparse_electron_akreyrium = StarTMaterials.SparseElectronAkreyrium;
        Material sparse_muon_akreyrium = StarTMaterials.SparseMuonAkreyrium;
        Material sparse_tau_akreyrium = StarTMaterials.SparseTauAkreyrium;

        MANIFOLD_CENTRIFUGE_RECIPE_TYPE.recipeBuilder("akreyrium_quantising")
                .inputFluids(lepton_sparse_akreyrium.getFluid(1000))
                .chancedFluidOutputLogic(ChanceLogic.XOR)
                .chancedOutput(sparse_electron_akreyrium.getFluid(1000), 20_00, 0)
                .chancedOutput(sparse_muon_akreyrium.getFluid(1000), 40_00, 0)
                .chancedOutput(sparse_tau_akreyrium.getFluid(1000), 99_99, 0)
                .duration(1200)
                .EUt(2097152)
                .save(provider);
    }
}
