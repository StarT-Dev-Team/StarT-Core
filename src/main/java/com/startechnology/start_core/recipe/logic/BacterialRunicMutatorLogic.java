package com.startechnology.start_core.recipe.logic;

import static com.startechnology.start_core.item.StarTBacteriaItems.BACTERIA_ITEMS;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.bacteria.StarTBacteriaManager;
import com.startechnology.start_core.api.bacteria.StarTBacteriaStats;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.item.components.StarTBacteriaBehaviour;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.tterrag.registrate.util.entry.ItemEntry;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

public class BacterialRunicMutatorLogic implements ICustomRecipeLogic {

    public BacterialRunicMutatorLogic() {

    }

    @Override
    public GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var handlers = Objects
                .requireNonNullElseGet(holder.getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP),
                        Collections::emptyList)
                .stream()
                .filter(NotifiableItemStackHandler.class::isInstance)
                .map(NotifiableItemStackHandler.class::cast)
                .filter(i -> i.getSlots() >= 1)
                .collect(Collectors.groupingBy(NotifiableRecipeHandlerTrait::isDistinct));

        if (handlers.isEmpty()) return null;

        // Distinct first, reset our stacks for every inventory
        for (var handler : handlers.getOrDefault(true, Collections.emptyList())) {
            ItemStack runic = ItemStack.EMPTY;
            ItemStack bacteria = ItemStack.EMPTY;
            GTRecipe recipe = createBacteriaRecipe(runic, bacteria, handler);
            if (recipe != null) return recipe;
        }

        // Non-distinct, return as soon as we find valid items
        ItemStack runic = ItemStack.EMPTY;
        ItemStack bacteria = ItemStack.EMPTY;
        for (var handler : handlers.getOrDefault(false, Collections.emptyList())) {
            GTRecipe recipe = createBacteriaRecipe(runic, bacteria, handler);
            if (recipe != null) return recipe;
        }

        return null;
    }

    public static GTRecipe createBacteriaRecipe(ItemStack existingRunic, ItemStack existingBacteria, NotifiableItemStackHandler handler) {
        // Find first items that match the mutation requirements
        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack itemInSlot = handler.getStackInSlot(i);
            
            if (!existingBacteria.isEmpty() && !existingRunic.isEmpty()) break;

            if (itemInSlot == null) continue;

            if (!itemInSlot.isEmpty()) {
                // Check for runic
                if (existingRunic.isEmpty() && isRunic(itemInSlot)) {
                    existingRunic = itemInSlot;
                    continue;
                }

                // Check for bacteria
                if (existingBacteria.isEmpty() && StarTBacteriaBehaviour.getBacteriaBehaviour(itemInSlot) != null) {
                    existingBacteria = itemInSlot;
                    continue;
                }
            }
        }


        if (!existingBacteria.isEmpty() && !existingRunic.isEmpty()) {
            StarTBacteriaBehaviour bacteriaBehaviour = StarTBacteriaBehaviour.getBacteriaBehaviour(existingBacteria);

            if (bacteriaBehaviour == null) return null;

            // Current stat for mutability
            StarTBacteriaStats existingStats = StarTBacteriaManager.bacteriaStatsFromTag(existingBacteria);

            if (existingStats == null) return null;

            // Affinity & stats are mutated always, so generate that
            Integer production = StarTCore.RNG.nextIntBetweenInclusive(1,  StarTBacteriaStats.MAX_STAT_VALUE);
            Integer metabolism = StarTCore.RNG.nextIntBetweenInclusive(1,  StarTBacteriaStats.MAX_STAT_VALUE);
            Integer mutability = StarTCore.RNG.nextIntBetweenInclusive(1,  StarTBacteriaStats.MAX_STAT_VALUE);

            List<Fluid> possibleAffinityFluids = bacteriaBehaviour.getBehaviourAffinityFluids();
            Collections.shuffle(possibleAffinityFluids);

            StarTBacteriaStats mutatedStats = new StarTBacteriaStats(production, metabolism, mutability,
                possibleAffinityFluids.get(0), possibleAffinityFluids.get(1), possibleAffinityFluids.get(2)
            );

            ItemStack netherstar = new ItemStack(ForgeRegistries.ITEMS.getValue(Minecraft.id("nether_star")));

            if (existingRunic.is(
                ForgeRegistries.FLUIDS.getValue(gtceu.id("enriched_naquadah"))
            )) {
                // Affinity & stat mutation only.
                ItemStack output = existingBacteria.copyWithCount(1);
                StarTBacteriaManager.writeBacteriaStatsToItem(output.getOrCreateTag(), mutatedStats);

                return StarTRecipeTypes.BACTERIAL_RUNIC_MUTATOR_RECIPES
                    .recipeBuilder("runic_mutator_pathway")
                    .inputItems(existingBacteria.copyWithCount(1))
                    .chancedInput(existingRunic.copyWithCount(1), 10_00, 0)
                    .inputFluids(GTMaterials.DistilledWater.getFluid(8000))
                    .inputFluids(GTMaterials.NaquadahEnriched.getFluid(400))
                    .outputItems(output)
                    .duration(400)
                    .EUt(GTValues.V[GTValues.UV])
                    .buildRawRecipe();
            }

            // Total mutation
            ItemEntry<ComponentItem> nextType = BACTERIA_ITEMS.get(
                StarTCore.RNG.nextIntBetweenInclusive(0, BACTERIA_ITEMS.size() - 1)
            );

            ItemStack output = new ItemStack(nextType.get());
            List<Fluid> possibleNewAffinities = StarTBacteriaBehaviour.getBacteriaBehaviour(output).getBehaviourAffinityFluids();
            Collections.shuffle(possibleNewAffinities);

            StarTBacteriaStats newStats = new StarTBacteriaStats(production, metabolism, mutability, 
                possibleAffinityFluids.get(0), possibleAffinityFluids.get(1), possibleAffinityFluids.get(2)
            );
            StarTBacteriaManager.writeBacteriaStatsToItem(output.getOrCreateTag(), newStats);

            return StarTRecipeTypes.BACTERIAL_RUNIC_MUTATOR_RECIPES
                .recipeBuilder("runic_mutator_total")
                .inputItems(existingBacteria.copyWithCount(1))
                .chancedInput(existingRunic.copyWithCount(1), 10_00, 0)
                .inputFluids(GTMaterials.DistilledWater.getFluid(8000))
                .inputFluids(GTMaterials.Naquadria.getFluid(800))
                .outputItems(output)
                .duration(640)
                .EUt(GTValues.V[GTValues.UV])
                .buildRawRecipe();
        }
        
        return null;
    }

    public static boolean isRunic(ItemStack potentialRunic) {
        return potentialRunic.is(
            ForgeRegistries.ITEMS.getValue(KubeJS.id("runic_engraved_plating"))
        ) || potentialRunic.is(
            ForgeRegistries.ITEMS.getValue(KubeJS.id("runic_pathway_engraved_plating"))
        );
    }

    @Override
    public void buildRepresentativeRecipes() {
        ItemStack runicPathway = new ItemStack(ForgeRegistries.ITEMS.getValue(KubeJS.id("runic_pathway_engraved_plating")));
        ItemStack runic = new ItemStack(ForgeRegistries.ITEMS.getValue(KubeJS.id("runic_engraved_plating")));

        BACTERIA_ITEMS.stream().forEach(
            bacteria -> {
                    ItemStack bacteriaInput = new ItemStack(bacteria.asItem());
                    StarTCustomTooltipsManager.writeCustomTooltipsToItem(bacteriaInput.getOrCreateTag(), 
                        "behaviour.start_core.bacteria.input");

                    ItemStack bacteriaAffinityMutationOutput = new ItemStack(bacteria.asItem());
                    StarTCustomTooltipsManager.writeCustomTooltipsToItem(bacteriaAffinityMutationOutput.getOrCreateTag(), 
                        "behaviour.start_core.bacteria.mutator_affinity_output"
                    );

                    ItemStack bacteriaTotalMutationOutput = new ItemStack(bacteria.asItem()); 
                    StarTCustomTooltipsManager.writeCustomTooltipsToItem(bacteriaTotalMutationOutput.getOrCreateTag(), 
                        "behaviour.start_core.bacteria.mutator_total_output"
                    );

                    bacteriaTotalMutationOutput.setHoverName(Component.translatable(
                        "behaviour.start_core.bacteria.mutator_total_output_generic_bacteria"
                    ));

                    GTRecipe affinityRecipe = StarTRecipeTypes.BACTERIAL_RUNIC_MUTATOR_RECIPES
                        .recipeBuilder(bacteria.getId().getPath().toString() + "_affinity")
                        .inputItems(bacteriaInput.copyWithCount(1))
                        .chancedInput(runicPathway, 10_00 ,0)
                        .inputFluids(GTMaterials.DistilledWater.getFluid(8000))
                        .inputFluids(GTMaterials.Mutagen.getFluid( 400))
                        .outputItems(bacteriaAffinityMutationOutput)
                        .duration(240)
                        .EUt(GTValues.V[GTValues.UV])
                        .buildRawRecipe();

                    GTRecipe totalRecipe = StarTRecipeTypes.BACTERIAL_RUNIC_MUTATOR_RECIPES
                        .recipeBuilder(bacteria.getId().getPath().toString() + "_total")
                        .inputItems(bacteriaInput.copyWithCount(1))
                        .chancedInput(runic, 10_00 ,0)
                        .inputFluids(GTMaterials.DistilledWater.getFluid(8000))
                        .inputFluids(GTMaterials.Mutagen.getFluid( 800))
                        .outputItems(bacteriaTotalMutationOutput)
                        .duration(240)
                        .EUt(GTValues.V[GTValues.UV])
                        .buildRawRecipe();

                    // for EMI to detect it's a synthetic recipe (not ever in JSON)
                    affinityRecipe.setId(affinityRecipe.getId().withPrefix("/"));
                    StarTRecipeTypes.BACTERIAL_RUNIC_MUTATOR_RECIPES.addToMainCategory(affinityRecipe);

                    totalRecipe.setId(totalRecipe.getId().withPrefix("/"));
                    StarTRecipeTypes.BACTERIAL_RUNIC_MUTATOR_RECIPES.addToMainCategory(totalRecipe);
            }
        );
    }
    
}
