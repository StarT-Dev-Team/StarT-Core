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
            .requireNonNullElseGet(holder.getCapabilitiesProxy().get(IO.IN, ItemRecipeCapability.CAP),
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

                FluidStack bacterialInputSludge = GTMaterials.EnrichedBacterialSludge.getFluid(
                    25 * (2 << existingStats.getMetabolism())
                );

                ItemStack sugar = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "sugar")), 
                    (2 << existingStats.getMetabolism())
                );

                FluidStack mainOutputStack = new FluidStack(
                    bacteriaBehaviour.getBehaviourMainFluid(), 
                    2000 * existingStats.getProduction()
                );

                FluidStack byproductOutputStack = new FluidStack(
                    existingStats.getAffinity(),
                    320 * existingStats.getProduction()
                );

                // Output
                return StarTRecipeTypes.BACTERIAL_HYDROCARBON_HARVESTER_RECIPES
                    .recipeBuilder("harvesting")
                    .inputItems(itemInSlot.copyWithCount(1))
                    .inputFluids(GTMaterials.DistilledWater.getFluid(1000))
                    .inputFluids(bacterialInputSludge)
                    .inputItems(sugar)
                    .outputFluids(byproductOutputStack)
                    .outputFluids(mainOutputStack)
                    .duration(240)
                    .EUt(GTValues.V[GTValues.ZPM])
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
                Fluid mainOutput = inputBehaviour.getBehaviourMainFluid();
                
                FluidStack bacterialInputSludge = GTMaterials.EnrichedBacterialSludge.getFluid(
                        25 * (2 << StarTBacteriaStats.MAX_STAT_VALUE)
                );

                StarTCustomTooltipsManager.writeCustomTooltipsToItem(
                    bacterialInputSludge.getOrCreateTag(), 
                    "behaviour.start_core.bacteria.maximum_shown_input",
                    "behaviour.start_core.bacteria.harvester_sludge_input"
                );

                ItemStack sugar = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "sugar")), 
                    (2 << StarTBacteriaStats.MAX_STAT_VALUE)
                );

                StarTCustomTooltipsManager.writeCustomTooltipsToItem(
                    sugar.getOrCreateTag(), 
                    "behaviour.start_core.bacteria.maximum_shown_input",
                    "behaviour.start_core.bacteria.harvester_sugar_input"
                );

                FluidStack mainOutputStack = new FluidStack(mainOutput, 
                    2000 * StarTBacteriaStats.MAX_STAT_VALUE
                );

                StarTCustomTooltipsManager.writeCustomTooltipsToItem(
                    mainOutputStack.getOrCreateTag(), 
                    "behaviour.start_core.bacteria.primary_output",
                    "behaviour.start_core.bacteria.maximum_shown_output",
                    "behaviour.start_core.bacteria.harvester_main_output"
                );

                List<Fluid> byproductOutputs = inputBehaviour.getBehaviourAffinityFluids();
                List<FluidStack> byproductOutputStacks = byproductOutputs.stream().map(
                    fluid -> {
                        FluidStack byproductStack = new FluidStack(fluid, 320 * StarTBacteriaStats.MAX_STAT_VALUE);

                        StarTCustomTooltipsManager.writeCustomTooltipsToItem(
                            byproductStack.getOrCreateTag(), 
                            "behaviour.start_core.bacteria.harvester_byproduct_output_title",
                            "behaviour.start_core.bacteria.harvester_byproduct_outputs",
                            "behaviour.start_core.bacteria.maximum_shown_output",
                            "behaviour.start_core.bacteria.harvester_byproduct_output"
                        );

                        return byproductStack;
                    }
                ).collect(Collectors.toList());

                GTRecipe harvesterRecipe = StarTRecipeTypes.BACTERIAL_HYDROCARBON_HARVESTER_RECIPES
                    .recipeBuilder(bacteria.getId().getPath().toString() + "_harvest")
                    .inputItems(bacteriaInput.copyWithCount(1))
                    .inputFluids(GTMaterials.DistilledWater.getFluid(1000))
                    .inputFluids(bacterialInputSludge)
                    .inputItems(sugar)
                    .outputFluids(mainOutputStack)
                    .outputFluids(byproductOutputStacks.toArray(new FluidStack[byproductOutputStacks.size()]))
                    .duration(240)
                    .EUt(GTValues.V[GTValues.ZPM])
                    .buildRawRecipe();
            
                harvesterRecipe.setId(harvesterRecipe.getId().withPrefix("/"));
                StarTRecipeTypes.BACTERIAL_HYDROCARBON_HARVESTER_RECIPES.addToMainCategory(harvesterRecipe);
            }
        );
    }
}
