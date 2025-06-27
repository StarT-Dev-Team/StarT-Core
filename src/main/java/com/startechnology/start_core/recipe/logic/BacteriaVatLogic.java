package com.startechnology.start_core.recipe.logic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.startechnology.start_core.api.bacteria.StarTBacteriaManager;
import com.startechnology.start_core.api.bacteria.StarTBacteriaStats;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import static com.startechnology.start_core.item.StarTBacteriaItems.BACTERIA_ITEMS;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

public class BacteriaVatLogic implements ICustomRecipeLogic {
    public static void bacterialBreeding() {
        BACTERIA_ITEMS.stream()
            .forEach(
                bacteria -> {
                    ItemStack bacteriaInput = new ItemStack(bacteria.asItem());
                    StarTCustomTooltipsManager.writeCustomTooltipsToItem(bacteriaInput.getOrCreateTag(), 
                        "behaviour.start_core.bacteria.input");

                    ItemStack bacteriaMutationOutput = new ItemStack(bacteria.asItem(), 16);
                    StarTCustomTooltipsManager.writeCustomTooltipsToItem(bacteriaMutationOutput.getOrCreateTag(), 
                        "behaviour.start_core.bacteria.vat_same_output"
                    );

                    ItemStack bacteriaReplicationOutput = new ItemStack(bacteria.asItem(), 16);
                    StarTCustomTooltipsManager.writeCustomTooltipsToItem(bacteriaReplicationOutput.getOrCreateTag(), 
                        "behaviour.start_core.bacteria.vat_mutated_output"
                    );
                    

                    GTRecipe recipe = StarTRecipeTypes.BACTERIAL_BREEDING_VAT_RECIPES
                        .recipeBuilder(bacteria.getId().getPath())
                        .inputItems(bacteriaInput)
                        .inputFluids(GTMaterials.Water.getFluid(8000))
                        .inputFluids(GTMaterials.Bacteria.getFluid(2000))
                        .outputItems(bacteriaMutationOutput)
                        .outputItems(bacteriaReplicationOutput)
                        .duration(800)
                        .EUt(GTValues.V[GTValues.UV])
                        .buildRawRecipe();

                     // for EMI to detect it's a synthetic recipe (not ever in JSON)
                    recipe.setId(recipe.getId().withPrefix("/"));
                    StarTRecipeTypes.BACTERIAL_BREEDING_VAT_RECIPES.addToMainCategory(recipe);
                }
            );
    }

    @Override
    public void buildRepresentativeRecipes() {
        bacterialBreeding();
    }

    @Override
    public GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
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
            GTRecipe recipe = createBacteriaRecipe(handler);
            if (recipe != null) return recipe;
        }

        return null;
    }

    private GTRecipe createBacteriaRecipe(NotifiableItemStackHandler handler) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack itemInSlot = handler.getStackInSlot(i);
            
            if (itemInSlot == null) continue;

            if (!itemInSlot.isEmpty()) {
                StarTBacteriaStats stats = StarTBacteriaManager.bacteriaStatsFromTag(itemInSlot);
            
                if (stats == null) continue;

                ItemStack outputMutatedBacteria = new ItemStack(itemInSlot.getItem(), 16);
                ItemStack outputReplicatedBacteria = new ItemStack(itemInSlot.getItem(), 16);
            
                // 16x of the same
                StarTBacteriaManager.writeBacteriaStatsToItem(outputReplicatedBacteria.getOrCreateTag(), stats);

                // IT's mathing time
                List<Double> weights = getMutationWeights(stats.getMutability());
                WeightedRandomList<Integer> productionOutputs = getStatWeightedList(stats.getProduction(), weights);
                WeightedRandomList<Integer> metabolismOutputs = getStatWeightedList(stats.getMetabolism(), weights);
                WeightedRandomList<Integer> mutabilityOutputs = getStatWeightedList(stats.getMutability(), weights);

                StarTBacteriaStats newStats = new StarTBacteriaStats(
                    productionOutputs.getRandom(),
                    metabolismOutputs.getRandom(),
                    mutabilityOutputs.getRandom(),
                    stats.getPrimary(),
                    stats.getSecondary(),
                    stats.getTertiary()
                );

                StarTBacteriaManager.writeBacteriaStatsToItem(outputMutatedBacteria.getOrCreateTag(), newStats);

                return StarTRecipeTypes.BACTERIAL_BREEDING_VAT_RECIPES.recipeBuilder(itemInSlot.getItem().getDescriptionId())
                    .inputItems(itemInSlot.copyWithCount(1))
                    .outputItems(outputReplicatedBacteria)
                    .outputItems(outputMutatedBacteria)
                    .inputFluids(GTMaterials.Water.getFluid(8000))
                    .inputFluids(GTMaterials.Bacteria.getFluid(2000))
                    .duration(800)
                    .EUt(GTValues.V[GTValues.UV])
                    .buildRawRecipe();
            }
        }

        return null;
    }

    private static List<Double> getMutationWeights(Integer mutability) {
        switch (mutability) {
            case 1:
                return Arrays.asList(0.0, 5.0, 90.0, 5.0, 0.0);
            case 2:
                return Arrays.asList(2.0, 8.0, 80.0, 8.0, 2.0);
            case 3:
                return Arrays.asList(5.0, 15.0, 60.0, 15.0, 5.0);
            case 4:
                return Arrays.asList(12.0, 18.0, 40.0, 18.0, 12.0);
            default:
                return Arrays.asList(20.0, 20.0, 20.0, 20.0, 20.0);
        }
    }

    private static WeightedRandomList<Integer> getStatWeightedList(Integer stat, List<Double> weights) {
        WeightedRandomList<Integer> statOutput = new WeightedRandomList<>();
        
        statOutput.addEntry(Math.max(1, stat - 2),  weights.get(0));
        statOutput.addEntry(Math.max(1, stat - 1),  weights.get(1));
        statOutput.addEntry(stat,                   weights.get(2));
        statOutput.addEntry(Math.min(5, stat + 1),  weights.get(3));
        statOutput.addEntry(Math.min(5, stat + 2),  weights.get(4));

        return statOutput;
    }

}