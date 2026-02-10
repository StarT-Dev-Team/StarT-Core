package com.startechnology.start_core.recipe.logic;

import static com.startechnology.start_core.item.StarTBacteriaItems.BACTERIA_ITEMS;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.startechnology.start_core.api.bacteria.StarTBacteriaManager;
import com.startechnology.start_core.api.bacteria.StarTBacteriaStats;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.item.components.StarTBacteriaBehaviour;
import com.startechnology.start_core.recipe.StarTRecipeTypes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class BacterialHydrocarbonHarvesterLogic implements ICustomRecipeLogic {

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        List<NotifiableItemStackHandler> handlers = Objects
            .requireNonNullElseGet(holder.getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP),
                    Collections::emptyList)
            .stream()
            .filter(NotifiableItemStackHandler.class::isInstance)
            .map(NotifiableItemStackHandler.class::cast)
            .filter(i -> i.getSlots() >= 1)
            .collect(Collectors.toList());

        if (handlers.isEmpty()) return null;

        // Return for the first recipe found
        for (NotifiableItemStackHandler handler : handlers) {
            GTRecipe recipe = createHarvesterRecipe(handler);
            if (recipe != null) return recipe;
        }

        return null;
    }
 
    private GTRecipe createHarvesterRecipe(NotifiableItemStackHandler handler) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack itemInSlot = handler.getStackInSlot(i);
            
            if (itemInSlot == null) continue;

            if (!itemInSlot.isEmpty()) {
                StarTBacteriaBehaviour bacteriaBehaviour = StarTBacteriaBehaviour.getBacteriaBehaviour(itemInSlot);

                if (bacteriaBehaviour == null) continue;
    
                // Current stats
                StarTBacteriaStats existingStats = StarTBacteriaManager.bacteriaStatsFromTag(itemInSlot);
    
                if (existingStats == null) continue;

                FluidStack biomass = GTMaterials.Biomass.getFluid(
                    100 * (2 << (existingStats.getMetabolism()-1))
                );

                ItemStack sugar = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "sugar")), 
                    (2 << (existingStats.getMetabolism()-1))
                );

                FluidStack primaryOutput = new FluidStack(
                    existingStats.getPrimary(), 
                    1500 * existingStats.getProduction()
                );

                FluidStack secondaryOutput = new FluidStack(
                    existingStats.getSecondary(),
                    750 * existingStats.getProduction()
                );

                FluidStack tertiaryOutput = new FluidStack(
                    existingStats.getTertiary(),
                    250 * existingStats.getProduction()
                );

                FluidStack superOutput = new FluidStack(
                        existingStats.getSuperFluid(),
                        1000 * existingStats.getProduction()
                );

                // Output
                return StarTRecipeTypes.BACTERIAL_HYDROCARBON_HARVESTER_RECIPES
                    .recipeBuilder("harvesting")
                    .inputItems(itemInSlot.copyWithCount(1))
                    .inputFluids(GTMaterials.DistilledWater.getFluid(1000))
                    .inputFluids(biomass)
                    .inputItems(sugar)
                    .outputFluids(primaryOutput, secondaryOutput, tertiaryOutput, superOutput, GTMaterials.Bacteria.getFluid(500))
                    .duration(160)
                    .EUt(GTValues.VH[GTValues.ZPM])
                    .buildRawRecipe();
            }
        }

        return null;
    }

    @Override
    public void buildRepresentativeRecipes() {
        BACTERIA_ITEMS.stream().forEach(
            bacteria -> {
                ItemStack bacteriaInput = new ItemStack(bacteria.asItem());
                StarTCustomTooltipsManager.writeCustomTooltipsToItem(bacteriaInput.getOrCreateTag(), 
                    "behaviour.start_core.bacteria.input");

                StarTBacteriaBehaviour inputBehaviour = StarTBacteriaBehaviour.getBacteriaBehaviour(bacteriaInput);
                List<Fluid> affinities = inputBehaviour.getBehaviourAffinityFluids();
                
                FluidStack biomass = GTMaterials.Biomass.getFluid(
                        100 * (2 << (StarTBacteriaStats.MAX_STAT_VALUE-1))
                );

                StarTCustomTooltipsManager.writeCustomTooltipsToItem(
                    biomass.getOrCreateTag(),
                    "behaviour.start_core.bacteria.maximum_shown_input",
                    "behaviour.start_core.bacteria.harvester_biomass_input"
                );

                ItemStack sugar = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "sugar")), 
                    (2 << (StarTBacteriaStats.MAX_STAT_VALUE-1))
                );

                StarTCustomTooltipsManager.writeCustomTooltipsToItem(
                    sugar.getOrCreateTag(), 
                    "behaviour.start_core.bacteria.maximum_shown_input",
                    "behaviour.start_core.bacteria.harvester_sugar_input"
                );

                FluidStack primaryOutputStack = new FluidStack(affinities.get(0), 
                    1500 * StarTBacteriaStats.MAX_STAT_VALUE
                );

                StarTCustomTooltipsManager.writeCustomTooltipsToItem(
                    primaryOutputStack.getOrCreateTag(), 
                    "behaviour.start_core.bacteria.primary_output",
                    "behaviour.start_core.bacteria.any_affinity",
                    "behaviour.start_core.bacteria.maximum_shown_output",
                    "behaviour.start_core.bacteria.harvester_primary_output"
                );


                FluidStack secondaryOutputStack = new FluidStack(affinities.get(1), 
                    750 * StarTBacteriaStats.MAX_STAT_VALUE
                );

                StarTCustomTooltipsManager.writeCustomTooltipsToItem(
                    secondaryOutputStack.getOrCreateTag(), 
                    "behaviour.start_core.bacteria.secondary_output",
                    "behaviour.start_core.bacteria.any_affinity",
                    "behaviour.start_core.bacteria.maximum_shown_output",
                    "behaviour.start_core.bacteria.harvester_secondary_output"
                );


                FluidStack tertiaryOutputStack = new FluidStack(affinities.get(2), 
                    250 * StarTBacteriaStats.MAX_STAT_VALUE
                );

                StarTCustomTooltipsManager.writeCustomTooltipsToItem(
                    tertiaryOutputStack.getOrCreateTag(), 
                    "behaviour.start_core.bacteria.tertiary_output",
                    "behaviour.start_core.bacteria.any_affinity",
                    "behaviour.start_core.bacteria.maximum_shown_output",
                    "behaviour.start_core.bacteria.harvester_tertiary_output"
                );


                FluidStack superOutputStack = new FluidStack(inputBehaviour.getSuperfluid(),
                        1000 * StarTBacteriaStats.MAX_STAT_VALUE
                );

                StarTCustomTooltipsManager.writeCustomTooltipsToItem(
                        superOutputStack.getOrCreateTag(),
                        "behaviour.start_core.bacteria.super_output",
                        "behaviour.start_core.bacteria.any_affinity",
                        "behaviour.start_core.bacteria.maximum_shown_output",
                        "behaviour.start_core.bacteria.harvester_super_output"
                );

                GTRecipe harvesterRecipe = StarTRecipeTypes.BACTERIAL_HYDROCARBON_HARVESTER_RECIPES
                    .recipeBuilder(bacteria.getId().getPath().toString() + "_harvest")
                    .inputItems(bacteriaInput.copyWithCount(1))
                    .inputFluids(GTMaterials.DistilledWater.getFluid(1000))
                    .inputFluids(biomass)
                    .inputItems(sugar)
                    .outputFluids(primaryOutputStack, secondaryOutputStack, tertiaryOutputStack, superOutputStack, GTMaterials.Bacteria.getFluid(500))
                    .duration(160)
                    .EUt(GTValues.VH[GTValues.ZPM])
                    .buildRawRecipe();
            
                harvesterRecipe.setId(harvesterRecipe.getId().withPrefix("/"));
                StarTRecipeTypes.BACTERIAL_HYDROCARBON_HARVESTER_RECIPES.addToMainCategory(harvesterRecipe);
            }
        );
    }
}
