package com.startechnology.start_core.recipe;
import java.util.function.Consumer;

import com.startechnology.start_core.recipe.recipes.AkreyriumLine;
import com.startechnology.start_core.recipe.recipes.DrumRecipes;
import com.startechnology.start_core.recipe.recipes.FluidCellRecipes;
import com.startechnology.start_core.recipe.recipes.ResetNBT;

import net.minecraft.data.recipes.FinishedRecipe;

public class StarTRecipes {
    public static final void init(Consumer<FinishedRecipe> provider) {
        ResetNBT.init(provider);
        AkreyriumLine.init(provider);
        DrumRecipes.init(provider);
        FluidCellRecipes.init(provider);
    }
}
