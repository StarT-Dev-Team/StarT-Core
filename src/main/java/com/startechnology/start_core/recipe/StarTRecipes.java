package com.startechnology.start_core.recipe;

import com.startechnology.start_core.recipe.recipes.*;
import com.startechnology.start_core.recipe.recipes.gcrops.*;
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
    }
}
