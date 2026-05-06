package com.startechnology.start_core.machine.steam;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.machine.multiblock.steam.SteamParallelMultiblockMachine;
import org.jetbrains.annotations.NotNull;

import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StarTSteamParallelMultiblockMachine extends SteamParallelMultiblockMachine {

    public int maxParallels = 6;

    // if in millibuckets, this is 3.0, Meaning 3mb of steam -> 1 EU
    public static final double CONVERSION_RATE = 3.0;

    public StarTSteamParallelMultiblockMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public double getConversionRate() {
        return CONVERSION_RATE;
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof StarTSteamParallelMultiblockMachine steamMachine)) {
            return RecipeModifier.nullWrongType(StarTSteamParallelMultiblockMachine.class, machine);
        }
        if (RecipeHelper.getRecipeEUtTier(recipe) > GTValues.LV) return ModifierFunction.NULL;

        long eut = recipe.getInputEUt().getTotalEU();
        int parallelAmount = ParallelLogic.getParallelAmount(machine, recipe, steamMachine.maxParallels);
        double eutMultiplier = (eut * 0.8888 * parallelAmount <= 32) ? (0.8888 * parallelAmount) : (32.0 / eut);
        return ModifierFunction.builder()
                .inputModifier(ContentModifier.multiplier(parallelAmount))
                .outputModifier(ContentModifier.multiplier(parallelAmount))
                .durationMultiplier(1.05)
                .eutMultiplier(eutMultiplier)
                .parallels(parallelAmount)
                .build();
    }
}

