package com.startechnology.start_core.recipe.logic;

import static com.startechnology.start_core.item.StarTBacteriaItems.BACTERIA_ITEMS;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class BacterialRunicMutatorLogic implements ICustomRecipeLogic {

    public BacterialRunicMutatorLogic() {

    }

    @Override
    public GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var itemHandlers = Objects
                .requireNonNullElseGet(holder.getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP),
                        Collections::emptyList)
                .stream()
                .filter(NotifiableItemStackHandler.class::isInstance)
                .map(NotifiableItemStackHandler.class::cast)
                .filter(i -> i.getSlots() >= 1)
                .collect(Collectors.groupingBy(NotifiableRecipeHandlerTrait::isDistinct));
        var fluidHandlers = Objects
                .requireNonNullElseGet(holder.getCapabilitiesFlat(IO.IN, FluidRecipeCapability.CAP),
                        Collections::emptyList)
                .stream()
                .filter(NotifiableFluidTank.class::isInstance)
                .map(NotifiableFluidTank.class::cast)
                .filter(i -> i.getTanks() >= 1)
                .collect(Collectors.groupingBy(NotifiableRecipeHandlerTrait::isDistinct));
        if (itemHandlers.isEmpty() || fluidHandlers.isEmpty()) return null;

        ItemStack bacteria = ItemStack.EMPTY;
        FluidStack naq = FluidStack.EMPTY;
        // Distinct first, reset our stacks for every inventory
        for (var itemHandler : itemHandlers.getOrDefault(true, Collections.emptyList())) {
            for (var fluidHandler : fluidHandlers.getOrDefault(true, Collections.emptyList())) {
                GTRecipe recipe = createBacteriaRecipe(bacteria, naq, itemHandler, fluidHandler);
                if (recipe != null) return recipe;
            }
            for (var fluidHandler : fluidHandlers.getOrDefault(false, Collections.emptyList())) {
                GTRecipe recipe = createBacteriaRecipe(bacteria, naq, itemHandler, fluidHandler);
                if (recipe != null) return recipe;
            }
        }

        // Non-distinct, return as soon as we find valid items
        for (var itemHandler : itemHandlers.getOrDefault(false, Collections.emptyList())) {
            for (var fluidHandler : fluidHandlers.getOrDefault(true, Collections.emptyList())) {
                GTRecipe recipe = createBacteriaRecipe(bacteria, naq, itemHandler, fluidHandler);
                if (recipe != null) return recipe;
            }
            for (var fluidHandler : fluidHandlers.getOrDefault(false, Collections.emptyList())) {
                GTRecipe recipe = createBacteriaRecipe(bacteria, naq, itemHandler, fluidHandler);
                if (recipe != null) return recipe;
            }
        }

        return null;
    }

    public static GTRecipe createBacteriaRecipe(ItemStack existingBacteria, FluidStack existingNaq, NotifiableItemStackHandler itemHandler, NotifiableFluidTank fluidHandler) {
        // Find first items that match the mutation requirements
        for (int i = 0; i < itemHandler.getSlots(); ++i) {
            ItemStack itemInSlot = itemHandler.getStackInSlot(i);
            
            if (!existingBacteria.isEmpty()) break;

            if (itemInSlot == null) continue;

            if (!itemInSlot.isEmpty()) {
                // Check for bacteria
                if (existingBacteria.isEmpty() && StarTBacteriaBehaviour.getBacteriaBehaviour(itemInSlot) != null) {
                    existingBacteria = itemInSlot;
                    continue;
                }
            }
        }

        for (int i = 0; i < fluidHandler.getTanks(); ++i) {
            FluidStack fluidInSlot = fluidHandler.getFluidInTank(i);

            if (!existingNaq.isEmpty()) break;

            if (fluidInSlot == null) continue;

            if (!fluidInSlot.isEmpty()) {
                // Check for naquadah
                if (existingNaq.isEmpty() && isNaq(fluidInSlot)) {
                    existingNaq = fluidInSlot;
                    continue;
                }
            }
        }


        if (existingBacteria.isEmpty() || existingNaq.isEmpty()) return null;

        StarTBacteriaBehaviour bacteriaBehaviour = StarTBacteriaBehaviour.getBacteriaBehaviour(existingBacteria);

        if (bacteriaBehaviour == null) return null;

        // Current stat for mutability
        StarTBacteriaStats existingStats = StarTBacteriaManager.bacteriaStatsFromTag(existingBacteria);

        if (existingStats == null) return null;

        Fluid superFluid = bacteriaBehaviour.getSuperfluid().getFluid();

        List<Fluid> possibleAffinityFluids = bacteriaBehaviour.getBehaviourAffinityFluids();
        Collections.shuffle(possibleAffinityFluids);

        // Affinity & stats are mutated always, so generate that
        Integer production = StarTCore.RNG.nextIntBetweenInclusive(1,  StarTBacteriaStats.MAX_STAT_VALUE);
        Integer metabolism = StarTCore.RNG.nextIntBetweenInclusive(1,  StarTBacteriaStats.MAX_STAT_VALUE);
        Integer mutability = StarTCore.RNG.nextIntBetweenInclusive(1,  StarTBacteriaStats.MAX_STAT_VALUE);

        StarTBacteriaStats mutatedStats = new StarTBacteriaStats(production, metabolism, mutability,
            possibleAffinityFluids.get(0), possibleAffinityFluids.get(1), possibleAffinityFluids.get(2), superFluid
        );

        ItemStack netherStar = new ItemStack(Items.NETHER_STAR);
        ItemStack newBacteria = existingBacteria.copyWithCount(1);

        if (existingNaq.isFluidEqual(GTMaterials.NaquadahEnriched.getFluid(1))) {
            // Affinity & stat mutation only.
            StarTBacteriaManager.writeBacteriaStatsToItem(newBacteria.getOrCreateTag(), mutatedStats);

            FluidStack enrichedNaq = existingNaq.copy();
            enrichedNaq.setAmount(400);

            return StarTRecipeTypes.BACTERIAL_RUNIC_MUTATOR_RECIPES
                .recipeBuilder("runic_mutator_pathway")
                .inputItems(existingBacteria.copyWithCount(1))
                .chancedInput(netherStar, 10_00, 0)
                .inputFluids(GTMaterials.DistilledWater.getFluid(8000))
                .inputFluids(enrichedNaq)
                .outputItems(newBacteria.copyWithCount(1))
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

        Fluid newSuperFluid = StarTBacteriaBehaviour.getBacteriaBehaviour(output).getSuperfluid().getFluid();

        StarTBacteriaStats newStats = new StarTBacteriaStats(production, metabolism, mutability,
                possibleAffinityFluids.get(0), possibleAffinityFluids.get(1), possibleAffinityFluids.get(2), newSuperFluid
        );
        StarTBacteriaManager.writeBacteriaStatsToItem(output.getOrCreateTag(), newStats);

        FluidStack naquadria = existingNaq.copy();
        naquadria.setAmount(400);

        return StarTRecipeTypes.BACTERIAL_RUNIC_MUTATOR_RECIPES
            .recipeBuilder("runic_mutator_total")
            .inputItems(existingBacteria.copyWithCount(1))
            .chancedInput(netherStar, 10_00, 0)
            .inputFluids(GTMaterials.DistilledWater.getFluid(8000))
            .inputFluids(naquadria)
            .outputItems(output)
            .duration(640)
            .EUt(GTValues.V[GTValues.UV])
            .buildRawRecipe();
    }

    public static boolean isNaq(FluidStack potentialNaq) {
        return potentialNaq.isFluidEqual(
                GTMaterials.NaquadahEnriched.getFluid(1)
        ) || potentialNaq.isFluidEqual(
                GTMaterials.Naquadria.getFluid(1)
        );
    }

    @Override
    public void buildRepresentativeRecipes() {
        ItemStack netherStar = new ItemStack(Items.NETHER_STAR);

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
                        .chancedInput(netherStar, 10_00 ,0)
                        .inputFluids(GTMaterials.DistilledWater.getFluid(8000))
                        .inputFluids(GTMaterials.NaquadahEnriched.getFluid(400))
                        .outputItems(bacteriaAffinityMutationOutput)
                        .duration(400)
                        .EUt(GTValues.V[GTValues.UV])
                        .buildRawRecipe();

                    GTRecipe totalRecipe = StarTRecipeTypes.BACTERIAL_RUNIC_MUTATOR_RECIPES
                        .recipeBuilder(bacteria.getId().getPath().toString() + "_total")
                        .inputItems(bacteriaInput.copyWithCount(1))
                        .chancedInput(netherStar, 10_00 ,0)
                        .inputFluids(GTMaterials.DistilledWater.getFluid(8000))
                        .inputFluids(GTMaterials.Naquadria.getFluid(800))
                        .outputItems(bacteriaTotalMutationOutput)
                        .duration(640)
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
