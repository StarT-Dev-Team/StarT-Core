package com.startechnology.start_core.recipe;

import com.startechnology.start_core.recipe.recipes.AkreyriumLine;
import com.startechnology.start_core.recipe.recipes.BlackHoleRecipes;
import com.startechnology.start_core.recipe.recipes.CrateRecipes;
import com.startechnology.start_core.recipe.recipes.CustomMaterialTypesRecipes;
import com.startechnology.start_core.recipe.recipes.DrumRecipes;
import com.startechnology.start_core.recipe.recipes.DustBlockRecipeHandler;
import com.startechnology.start_core.recipe.recipes.DysonSphereRecipes;
import com.startechnology.start_core.recipe.recipes.FluidCellRecipes;
import com.startechnology.start_core.recipe.recipes.NeutronStarRecipes;
import com.startechnology.start_core.recipe.recipes.ResetNBT;
import com.startechnology.start_core.recipe.recipes.gcrops.FlowerRecipes;
import com.startechnology.start_core.recipe.recipes.gcrops.GCropRecipes;
import com.startechnology.start_core.recipe.recipes.gcrops.GenomeHolderRecipes;

import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

public class StarTRecipes {

    public static final void init(Consumer<FinishedRecipe> provider) {
        ResetNBT.init(provider);
        AkreyriumLine.init(provider);
        DrumRecipes.init(provider);
        FluidCellRecipes.init(provider);
        CrateRecipes.init(provider);
        DustBlockRecipeHandler.init(provider);
        CustomMaterialTypesRecipes.init(provider);
        FlowerRecipes.init(provider);
        GCropRecipes.init(provider);
        GenomeHolderRecipes.init(provider);
        BlackHoleRecipes.init(provider);
        DysonSphereRecipes.init(provider);
        NeutronStarRecipes.init(provider);
    }
}
