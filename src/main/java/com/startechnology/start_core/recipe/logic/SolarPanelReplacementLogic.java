package com.startechnology.start_core.recipe.logic;

import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.block.solar.StarTSolarCellBlocks;
import com.startechnology.start_core.recipe.StarTRecipeTypes;

import net.minecraft.world.item.ItemStack;

public class SolarPanelReplacementLogic implements GTRecipeType.ICustomRecipeLogic {

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        return null;
    }

    @Override
    public void buildRepresentativeRecipes() {
        for (var cell : StarTSolarCellBlocks.SOLAR_CELLS.values()) {
            ItemStack newCell = new ItemStack(cell.get());
            ItemStack brokenCell = new ItemStack(cell.get());

            StarTCustomTooltipsManager.writeCustomTooltipsToItem(brokenCell.getOrCreateTag(),
                    "solar.start_core.solar_cell.is_broken");

            GTRecipe recipe = StarTRecipeTypes.SOLAR_PANEL_REPLACEMENT.recipeBuilder(newCell.getDescriptionId())
                    .inputItems(newCell)
                    .outputItems(brokenCell)
                    .duration(120)
                    .buildRawRecipe();

            recipe.setId(recipe.getId().withPrefix("/"));
            StarTRecipeTypes.SOLAR_PANEL_REPLACEMENT.addToMainCategory(recipe);
        }
    }
    
}
