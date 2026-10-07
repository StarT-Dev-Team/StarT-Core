package com.startechnology.start_core.recipe.logic.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.api.gcrop.*;
import com.startechnology.start_core.data.gcrops.StarTTraitData;
import com.startechnology.start_core.item.gcrops.StarTGCropItems;
import com.startechnology.start_core.item.components.StarTGCropBehaviour;
import com.startechnology.start_core.recipe.StarTRecipeTypes;

import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;

import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;
import static com.startechnology.start_core.item.gcrops.StarTGCropItems.GCROP_MALFORMED;

public class GCropMutatorLogic implements ICustomRecipeLogic {

    private static List<Item> VALID_MUTATION_ITEMS;

    /**
     * Lazily initializes and returns the list of mutation dust items corresponding to mutation tiers 1 through 8.
     *
     * @return an immutable list of mutation item catalysts
     */
    private static List<Item> getValidMutationItems() {
        if (VALID_MUTATION_ITEMS == null) VALID_MUTATION_ITEMS = List.of(
                ChemicalHelper.get(dust, EnderPearl).getItem(),
                ChemicalHelper.get(dust, Thorium).getItem(),
                ChemicalHelper.get(dust, Caesium).getItem(),
                ChemicalHelper.get(dust, Tantalum).getItem(),
                ChemicalHelper.get(dust, Uranium235).getItem(),
                ChemicalHelper.get(dust, PurifiedNaquadah).getItem(),
                ChemicalHelper.get(dust, Fermium).getItem(),
                ChemicalHelper.get(dust, Polonium).getItem());

        return VALID_MUTATION_ITEMS;
    }

    private static List<Fluid> VALID_MUTATION_FLUIDS;

    /**
     * Lazily initializes and returns the list of mutation fluids corresponding to mutation tiers 2 through 8.
     *
     * @return an immutable list of mutation fluid catalysts
     */
    private static List<Fluid> getValidMutationFluids() {
        if (VALID_MUTATION_FLUIDS == null) VALID_MUTATION_FLUIDS = List.of(
                Arsenic.getFluid(),
                Fluorine.getFluid(),
                Radon.getFluid(),
                IndiumGalliumPhosphide.getFluid(),
                Naquadria.getFluid(),
                Echo.getFluid(),
                BecOg.getFluid());

        return VALID_MUTATION_FLUIDS;
    }

    private enum MutationType {
        FULL,
        PROD,
        AUX
    }

    /**
     * Attempts to find a matching mutator recipe given the available item and fluid inputs.
     *
     * @param holder the recipe capability holder representing the mutator machine
     * @return the matched mutator {@link GTRecipe}, or {@code null} if no valid mutation can be performed
     */
    @Override
    public GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var itemHandlers = StarTCustomLogicUtils.getItemHandlersMap(holder);
        var fluidHandlers = StarTCustomLogicUtils.getFluidHandlersMap(holder);

        if (itemHandlers.isEmpty() || fluidHandlers.isEmpty()) return null;

        List<List<ItemStack>> allItems = StarTCustomLogicUtils.getAllItems(itemHandlers);
        List<FluidStack> allFluids = StarTCustomLogicUtils.getAllFluids(fluidHandlers);

        for (List<ItemStack> itemSet : allItems) {
            GTRecipe recipe = createGCropRecipe(itemSet, allFluids);
            if (recipe != null) return recipe;
        }

        return null;
    }

    /**
     * Checks if the given {@link FluidStack} matches any fluid in the target list by fluid type equality.
     *
     * @param fluid     the fluid stack to find
     * @param fluidList the list of candidate fluid stacks
     * @return {@code true} if a matching fluid is found, {@code false} otherwise
     */
    public static boolean hasFluidMatch(FluidStack fluid, List<FluidStack> fluidList) {
        for (FluidStack newFluid : fluidList) {
            if (fluid.isFluidEqual(newFluid)) return true;
        }
        return false;
    }

    /**
     * Checks if the given {@link Fluid} matches any fluid in the target fluid list.
     *
     * @param fluid     the fluid to find
     * @param fluidList the list of candidate fluids
     * @return {@code true} if a matching fluid is present, {@code false} otherwise
     */
    public static boolean hasFluidMatch(Fluid fluid, List<Fluid> fluidList) {
        for (Fluid newFluid : fluidList) {
            if (fluid.isSame(newFluid)) return true;
        }
        return false;
    }

    /**
     * Determines the mutation tier supported by the available item and fluid catalysts for a given mutation type.
     *
     * @param mutationItems  the mutation items present in the machine
     * @param mutationFluids the mutation fluids present in the machine
     * @param type           the type of mutation being performed (FULL, PROD, or AUX)
     * @return the determined mutation tier, or 0 if inputs are insufficient
     */
    private static int findTraitTier(List<ItemStack> mutationItems, List<FluidStack> mutationFluids,
                                     MutationType type) {
        List<Item> validItems = getValidMutationItems();
        List<Fluid> validFluids = getValidMutationFluids();

        int maxTier = 0;
        List<Integer> tiers = new ArrayList<>();

        if (type != MutationType.AUX) {
            for (var item : mutationItems) {
                int index = validItems.indexOf(item.getItem());
                if (index != -1) {
                    int tier = index + 1;
                    if (type != MutationType.FULL && maxTier < tier) {
                        maxTier = tier;
                    } else {
                        tiers.add(tier);
                    }
                }
            }
        }

        if (type != MutationType.PROD) {
            for (var fluid : mutationFluids) {
                int index = validFluids.indexOf(fluid.getFluid());
                if (index != -1) {
                    int tier = index + 2;
                    if (type != MutationType.FULL && maxTier < tier) {
                        maxTier = tier;
                    } else {
                        if (tiers.contains(tier)) {
                            maxTier = tier;
                        }
                    }
                }
            }
        }

        if (tiers.contains(1)) maxTier = 1;

        return maxTier;
    }

    /**
     * Randomly rolls a new climate trait weighted by trait frequency among all registered climate traits.
     *
     * @return the chosen {@link StarTGCropTrait}, or {@code null} if none was rolled
     */
    private static StarTGCropTrait rollClimateTrait() {
        List<StarTGCropTrait> climateTraits = new ArrayList<>(
                StarTGCropTraits.getTraitsByType(StarTTraitData.GenomeType.CLIMATE));

        int totalFrequency = 5000;
        for (var trait : climateTraits) {
            totalFrequency += trait.frequency();
        }

        Collections.shuffle(climateTraits);

        int hitFrequency = StarTCore.RNG.nextIntBetweenInclusive(1, totalFrequency);
        for (var trait : climateTraits) {
            int frequency = trait.frequency();
            if (hitFrequency <= frequency) {
                return trait;
            }
            hitFrequency -= frequency;
        }
        return null;
    }

    /**
     * Filters a genome to only retain genes whose traits are below the specified tier threshold.
     *
     * @param genome        the source genome
     * @param tierExclusive the exclusive upper tier bound
     * @return a list containing only genes below the given tier
     */
    private static List<StarTGCropGene> filterGenesBelowTier(List<StarTGCropGene> genome, int tierExclusive) {
        List<StarTGCropGene> filtered = new ArrayList<>(genome.size());
        for (StarTGCropGene gene : genome) {
            if (gene.getTrait().tier() < tierExclusive) {
                filtered.add(gene);
            }
        }
        return filtered;
    }

    /**
     * Executes frequency rolls for a list of candidate traits and adds any successful rolls to the target genome.
     *
     * @param traits       the candidate traits to evaluate
     * @param targetGenome the genome list to append newly generated genes to
     */
    private static void rollAndAddTraits(List<StarTGCropTrait> traits, List<StarTGCropGene> targetGenome) {
        for (var trait : traits) {
            int alleleCount = trait.runTraitFrequencyRandomGene();
            if (alleleCount >= 1) {
                targetGenome.add(new StarTGCropGene(trait, alleleCount));
            }
        }
    }

    /**
     * Evaluates inputs to construct an initial genome creation, full mutation, production/auxiliary mutation,
     * production-only mutation, or auxiliary-only mutation recipe.
     *
     * @param itemSet   the input item stacks in the machine
     * @param allFluids the input fluid stacks in the machine
     * @return the constructed {@link GTRecipe}, or {@code null} if no valid mutation recipe matches
     */
    public static GTRecipe createGCropRecipe(List<ItemStack> itemSet, List<FluidStack> allFluids) {
        List<Item> validMutationItemList = getValidMutationItems();
        List<Fluid> validMutationFluidList = getValidMutationFluids();

        ItemStack foundGCrop = ItemStack.EMPTY;
        List<ItemStack> validMutationItems = new ArrayList<>();
        List<FluidStack> validMutationFluids = new ArrayList<>();

        for (ItemStack item : itemSet) {
            if (StarTGCropBehaviour.getGCropBehaviour(item) != null) {
                foundGCrop = item;
            } else if (validMutationItemList.contains(item.getItem())) {
                validMutationItems.add(item);
            }
        }

        for (FluidStack fluid : allFluids) {
            if (hasFluidMatch(fluid.getFluid(), validMutationFluidList)) {
                validMutationFluids.add(fluid);
            }
        }

        if (foundGCrop.isEmpty() || (validMutationItems.isEmpty() && validMutationFluids.isEmpty())) return null;

        StarTGCropGenome existingStats = StarTGCropManager.gcropGenomeFromTag(foundGCrop);
        if (existingStats == null) {
            List<StarTGCropGene> emptyTraits = Collections.emptyList();
            ItemStack newGCrop = StarTGCropTraits.getCropWithTraits(emptyTraits, emptyTraits, emptyTraits);

            return StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                    .recipeBuilder("create_genome")
                    .inputItems(foundGCrop.copyWithCount(1))
                    .outputItems(newGCrop.copyWithCount(1))
                    .duration(400)
                    .EUtV(GTValues.MV)
                    .buildRawRecipe();
        }

        List<StarTGCropGene> existingResourceGenome = existingStats.getResourceGenome();
        List<StarTGCropGene> existingProductionGenome = existingStats.getProductionGenome();
        List<StarTGCropGene> existingAuxiliaryGenome = existingStats.getAuxiliaryGenome();

        StarTGCropTrait newClimateGenome = rollClimateTrait();

        ItemStack newGCrop;
        if (!validMutationItems.isEmpty() && !validMutationFluids.isEmpty()) {
            int maxTier = findTraitTier(validMutationItems, validMutationFluids, MutationType.FULL);
            if (maxTier == 0) return null;

            List<StarTGCropTrait> mutatedTraits = StarTGCropTraits
                    .getTraitsBetweenTiersInclusive(maxTier - 1, maxTier);

            if (hasFluidMatch(MysticalAir.getFluid(1), validMutationFluids)) {
                // full recipes
                List<StarTGCropGene> newResourceGenome = filterGenesBelowTier(existingResourceGenome, maxTier - 1);
                List<StarTGCropGene> newProductionGenome = filterGenesBelowTier(existingProductionGenome, maxTier - 1);
                List<StarTGCropGene> newAuxiliaryGenome = filterGenesBelowTier(existingAuxiliaryGenome, maxTier - 1);

                for (var trait : mutatedTraits) {
                    int alleleCount = trait.runTraitFrequencyRandomGene();
                    if (alleleCount >= 1) {
                        switch (trait.genomeType()) {
                            case RESOURCE -> newResourceGenome.add(new StarTGCropGene(trait, alleleCount));
                            case PRODUCTION -> newProductionGenome.add(new StarTGCropGene(trait, alleleCount));
                            case AUXILIARY -> newAuxiliaryGenome.add(new StarTGCropGene(trait, alleleCount));
                        }
                    }
                }

                newGCrop = StarTGCropTraits.getCropWithTraits(newResourceGenome, newProductionGenome,
                        newAuxiliaryGenome, new StarTGCropGene(newClimateGenome, 1));

                GTRecipeBuilder mutatorRecipe = StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                        .recipeBuilder(String.format("full_mutation_%s_to_%s", maxTier - 1, maxTier))
                        .inputItems(foundGCrop.copyWithCount(1))
                        .inputItems(new ItemStack(validMutationItemList.get(maxTier - 1)))
                        .inputFluids(MysticalAir.getFluid(1000))
                        .outputItems(newGCrop.copyWithCount(1))
                        .duration(400)
                        .EUtV(StarTGCropItems.tierVoltages.get(maxTier));

                if (maxTier >= 2)
                    mutatorRecipe.inputFluids(new FluidStack(validMutationFluidList.get(maxTier - 2), 1000));

                return mutatorRecipe.buildRawRecipe();
            }

            // prod aux recipes
            List<StarTGCropGene> newProductionGenome = filterGenesBelowTier(existingProductionGenome, maxTier - 1);
            List<StarTGCropGene> newAuxiliaryGenome = filterGenesBelowTier(existingAuxiliaryGenome, maxTier - 1);

            List<StarTGCropTrait> productionTraits = StarTGCropTraits
                    .getTraitsByType(StarTTraitData.GenomeType.PRODUCTION, mutatedTraits);
            List<StarTGCropTrait> auxiliaryTraits = StarTGCropTraits
                    .getTraitsByType(StarTTraitData.GenomeType.AUXILIARY, mutatedTraits);

            rollAndAddTraits(productionTraits, newProductionGenome);
            rollAndAddTraits(auxiliaryTraits, newAuxiliaryGenome);

            newGCrop = StarTGCropTraits.getCropWithTraits(existingResourceGenome, newProductionGenome,
                    newAuxiliaryGenome, new StarTGCropGene(newClimateGenome, 1));

            GTRecipeBuilder mutatorRecipe = StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                    .recipeBuilder(String.format("prod_aux_mutation_%s_to_%s", maxTier - 1, maxTier))
                    .inputItems(foundGCrop.copyWithCount(1))
                    .inputItems(new ItemStack(validMutationItemList.get(maxTier - 1)))
                    .outputItems(newGCrop.copyWithCount(1))
                    .duration(400)
                    .EUtV(StarTGCropItems.tierVoltages.get(maxTier));

            if (maxTier >= 2) mutatorRecipe.inputFluids(new FluidStack(validMutationFluidList.get(maxTier - 2), 1000));

            return mutatorRecipe.buildRawRecipe();
        }

        if (!validMutationItems.isEmpty()) {
            // prod recipes
            int maxTier = findTraitTier(validMutationItems, validMutationFluids, MutationType.PROD);
            if (maxTier == 0) return null;

            List<StarTGCropTrait> mutatedTraits = StarTGCropTraits
                    .getTraitsBetweenTiersInclusive(maxTier - 2, maxTier);

            List<StarTGCropGene> newProductionGenome = filterGenesBelowTier(existingProductionGenome, maxTier - 2);

            List<StarTGCropTrait> productionTraits = StarTGCropTraits
                    .getTraitsByType(StarTTraitData.GenomeType.PRODUCTION, mutatedTraits);

            rollAndAddTraits(productionTraits, newProductionGenome);

            newGCrop = StarTGCropTraits.getCropWithTraits(existingResourceGenome, newProductionGenome,
                    existingAuxiliaryGenome, new StarTGCropGene(newClimateGenome, 1));

            GTRecipeBuilder mutatorRecipe = StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                    .recipeBuilder(String.format("prod_mutation_%s_to_%s", maxTier - 2, maxTier))
                    .inputItems(foundGCrop.copyWithCount(1))
                    .inputItems(new ItemStack(validMutationItemList.get(maxTier - 1)))
                    .outputItems(newGCrop.copyWithCount(1))
                    .duration(400)
                    .EUtV(StarTGCropItems.tierVoltages.get(maxTier));

            return mutatorRecipe.buildRawRecipe();
        }

        // aux recipes
        int maxTier = findTraitTier(validMutationItems, validMutationFluids, MutationType.AUX);
        if (maxTier == 0) return null;

        List<StarTGCropTrait> mutatedTraits = StarTGCropTraits
                .getTraitsBetweenTiersInclusive(maxTier - 2, maxTier);

        List<StarTGCropGene> newAuxiliaryGenome = filterGenesBelowTier(existingAuxiliaryGenome, maxTier - 2);

        List<StarTGCropTrait> auxiliaryTraits = StarTGCropTraits
                .getTraitsByType(StarTTraitData.GenomeType.AUXILIARY, mutatedTraits);

        rollAndAddTraits(auxiliaryTraits, newAuxiliaryGenome);

        newGCrop = StarTGCropTraits.getCropWithTraits(existingResourceGenome, existingProductionGenome,
                newAuxiliaryGenome, new StarTGCropGene(newClimateGenome, 1));

        GTRecipeBuilder mutatorRecipe = StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                .recipeBuilder(String.format("aux_mutation_%s_to_%s", maxTier - 2, maxTier))
                .inputItems(foundGCrop.copyWithCount(1))
                .inputFluids(new FluidStack(validMutationFluidList.get(maxTier - 2), 1000))
                .outputItems(newGCrop.copyWithCount(1))
                .duration(400)
                .EUtV(StarTGCropItems.tierVoltages.get(maxTier));

        return mutatorRecipe.buildRawRecipe();
    }

    @Override
    public void buildRepresentativeRecipes() {
        List<Item> validMutationItemList = getValidMutationItems();
        List<Fluid> validMutationFluidList = getValidMutationFluids();

        ItemStack gCropRandomSeed = GCROP_MALFORMED.asStack();
        gCropRandomSeed.setHoverName(Component.translatable(
                "behaviour.start_core.gcrop.random_crop_name"));

        for (int i = 1; i <= 8; i++) {
            ItemStack fullMutatedSeed = GCROP_MALFORMED.asStack();
            fullMutatedSeed.setHoverName(Component.translatable(
                    "behaviour.start_core.gcrop.random_crop_name"));

            StarTCustomTooltipsManager.writeCustomTooltipsToItem(fullMutatedSeed.getOrCreateTag(),
                    Component.translatable("behaviour.start_core.gcrop.mutator.full", i - 1, i).getString());

            GTRecipeBuilder fullMutationRecipe = StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                    .recipeBuilder(String.format("full_mutation_%s_to_%s", i - 1, i))
                    .inputItems(gCropRandomSeed)
                    .inputItems(new ItemStack(validMutationItemList.get(i - 1)))
                    .inputFluids(MysticalAir.getFluid(1000))
                    .outputItems(fullMutatedSeed)
                    .duration(400)
                    .EUtV(StarTGCropItems.tierVoltages.get(i));

            if (i >= 2) fullMutationRecipe.inputFluids(new FluidStack(validMutationFluidList.get(i - 2), 1000));

            StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GCROP_MUTATOR_RECIPES, "gcrops",
                    fullMutationRecipe.buildRawRecipe());

            ItemStack prodAuxMutatedSeed = GCROP_MALFORMED.asStack();
            prodAuxMutatedSeed.setHoverName(Component.translatable(
                    "behaviour.start_core.gcrop.random_crop_name"));

            StarTCustomTooltipsManager.writeCustomTooltipsToItem(prodAuxMutatedSeed.getOrCreateTag(),
                    Component.translatable("behaviour.start_core.gcrop.mutator.prod_aux", i - 1, i).getString());

            GTRecipeBuilder prodAuxMutationRecipe = StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                    .recipeBuilder(String.format("prod_aux_mutation_%s_to_%s", i - 1, i))
                    .inputItems(gCropRandomSeed)
                    .inputItems(new ItemStack(validMutationItemList.get(i - 1)))
                    .outputItems(prodAuxMutatedSeed)
                    .duration(400)
                    .EUtV(StarTGCropItems.tierVoltages.get(i));

            if (i >= 2) prodAuxMutationRecipe.inputFluids(new FluidStack(validMutationFluidList.get(i - 2), 1000));

            StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GCROP_MUTATOR_RECIPES, "gcrops",
                    prodAuxMutationRecipe.buildRawRecipe());

            if (i > 1) {
                ItemStack prodMutatedSeed = GCROP_MALFORMED.asStack();
                prodMutatedSeed.setHoverName(Component.translatable(
                        "behaviour.start_core.gcrop.random_crop_name"));

                StarTCustomTooltipsManager.writeCustomTooltipsToItem(prodMutatedSeed.getOrCreateTag(),
                        Component.translatable("behaviour.start_core.gcrop.mutator.prod", i - 2, i).getString());

                ItemStack auxMutatedSeed = GCROP_MALFORMED.asStack();
                auxMutatedSeed.setHoverName(Component.translatable(
                        "behaviour.start_core.gcrop.random_crop_name"));

                StarTCustomTooltipsManager.writeCustomTooltipsToItem(auxMutatedSeed.getOrCreateTag(),
                        Component.translatable("behaviour.start_core.gcrop.mutator.aux", i - 2, i).getString());

                GTRecipe prodMutationRecipe = StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                        .recipeBuilder(String.format("prod_mutation_%s_to_%s", i - 2, i))
                        .inputItems(gCropRandomSeed)
                        .inputItems(new ItemStack(validMutationItemList.get(i - 1)))
                        .outputItems(prodMutatedSeed)
                        .duration(400)
                        .EUtV(StarTGCropItems.tierVoltages.get(i))
                        .buildRawRecipe();

                GTRecipe auxMutationRecipe = StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                        .recipeBuilder(String.format("aux_mutation_%s_to_%s", i - 2, i))
                        .inputItems(gCropRandomSeed)
                        .inputFluids(new FluidStack(validMutationFluidList.get(i - 2), 1000))
                        .outputItems(auxMutatedSeed)
                        .duration(400)
                        .EUtV(StarTGCropItems.tierVoltages.get(i))
                        .buildRawRecipe();

                StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GCROP_MUTATOR_RECIPES, "gcrops",
                        prodMutationRecipe);

                StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GCROP_MUTATOR_RECIPES, "gcrops",
                        auxMutationRecipe);
            }
        }
    }
}
