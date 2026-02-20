package com.startechnology.start_core.recipe;
import java.util.function.Consumer;

import com.startechnology.start_core.machine.crates.StarTCrates;
import com.startechnology.start_core.recipe.recipes.*;

import net.minecraft.data.recipes.FinishedRecipe;

public class StarTRecipes {
    public static final void init(Consumer<FinishedRecipe> provider) {
        ResetNBT.init(provider);
        AkreyriumLine.init(provider);
        DrumRecipes.init(provider);
        FluidCellRecipes.init(provider);
        CrateRecipes.init(provider);
        DustBlockRecipeHandler.init(provider);
    }
}
