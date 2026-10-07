package com.startechnology.start_core.recipe.logic.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.api.gcrop.*;
import com.startechnology.start_core.item.components.StarTGCropBehaviour;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

import static com.startechnology.start_core.item.gcrops.StarTGCropItems.GCROP_MALFORMED;

public class GCropBreederLogic implements GTRecipeType.ICustomRecipeLogic {

    @Override
    public GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var itemHandlers = StarTCustomLogicUtils.getItemHandlersMap(holder);
        if (itemHandlers.isEmpty()) return null;

        List<List<ItemStack>> allItems = StarTCustomLogicUtils.getAllItems(itemHandlers);
        for (List<ItemStack> itemSet : allItems) {
            GTRecipe recipe = createBreederRecipe(itemSet);
            if (recipe != null) return recipe;
        }

        return null;
    }

    /**
     * Applies meiotic allele segregation to an entire genome list, randomly inheriting alleles up to half
     * the trait's maximum allele count and merging them into the target trait map.
     *
     * @param genome  the source list of genes from a parent crop
     * @param geneMap the accumulator map for inherited traits and allele counts
     */
    private static void applyMeiosis(List<StarTGCropGene> genome, Map<StarTGCropTrait, Integer> geneMap) {
        for (StarTGCropGene gene : genome) {
            int alleles = gene.getDominantAlleles();
            StarTGCropTrait trait = gene.getTrait();
            int maxAlleles = trait.alleleCount();

            int alleleAddition = 0;
            for (int i = 0; i < alleles; i++) {
                if (alleleAddition * 2 >= maxAlleles) break;
                if (StarTCore.RNG.nextBoolean()) {
                    alleleAddition++;
                }
            }

            if (alleleAddition > 0) {
                geneMap.merge(trait, alleleAddition, (curr, add) -> Math.min(curr + add, maxAlleles));
            }
        }
    }

    /**
     * Converts a map of traits and allele counts into a list of {@link StarTGCropGene} instances.
     *
     * @param geneMap the trait-to-allele count map
     * @return a newly constructed list of genes
     */
    private static List<StarTGCropGene> geneMapToGenome(Map<StarTGCropTrait, Integer> geneMap) {
        List<StarTGCropGene> newGenome = new ArrayList<>(geneMap.size());
        for (Map.Entry<StarTGCropTrait, Integer> entry : geneMap.entrySet()) {
            newGenome.add(new StarTGCropGene(entry.getKey(), entry.getValue()));
        }
        return newGenome;
    }

    /**
     * Randomly retains or drops genes from a genome during self-fertilization based on a roll threshold.
     *
     * @param genome  the source list of genes
     * @param minRoll the minimum roll (1-100) required to keep the gene
     * @return the filtered list of surviving genes
     */
    private static List<StarTGCropGene> filterGenesRandomly(List<StarTGCropGene> genome, int minRoll) {
        List<StarTGCropGene> result = new ArrayList<>(genome.size());
        for (StarTGCropGene gene : genome) {
            if (StarTCore.RNG.nextIntBetweenInclusive(1, 100) >= minRoll) {
                result.add(gene);
            }
        }
        return result;
    }

    /**
     * Creates a breeding recipe from the provided input items, handling both cross-breeding
     * (when 2 valid crops are present) and self-fertilization (when 1 valid crop is present).
     *
     * @param itemSet the list of input items available in the machine
     * @return the constructed {@link GTRecipe}, or {@code null} if inputs do not match
     */
    private GTRecipe createBreederRecipe(List<ItemStack> itemSet) {
        List<ItemStack> foundCrops = new ArrayList<>(2);

        for (ItemStack itemInSlot : itemSet) {
            if (itemInSlot.isEmpty()) continue;

            if (StarTGCropBehaviour.getGCropBehaviour(itemInSlot) == null) continue;
            if (StarTGCropManager.gcropGenomeFromTag(itemInSlot) == null) continue;

            foundCrops.add(itemInSlot);
            if (foundCrops.size() == 2) break;
        }

        if (foundCrops.size() == 2) {
            Map<StarTGCropTrait, Integer> resourceGeneMap = new HashMap<>();
            Map<StarTGCropTrait, Integer> productionGeneMap = new HashMap<>();
            Map<StarTGCropTrait, Integer> auxiliaryGeneMap = new HashMap<>();

            StarTGCropGene newClimateGenome = null;

            for (ItemStack crop : foundCrops) {
                StarTGCropGenome cropStats = StarTGCropManager.gcropGenomeFromTag(crop);
                if (cropStats == null) continue;

                applyMeiosis(cropStats.getResourceGenome(), resourceGeneMap);
                applyMeiosis(cropStats.getProductionGenome(), productionGeneMap);
                applyMeiosis(cropStats.getAuxiliaryGenome(), auxiliaryGeneMap);

                if (newClimateGenome == null && cropStats.getClimateGene() != null) {
                    if (StarTCore.RNG.nextBoolean()) {
                        newClimateGenome = cropStats.getClimateGene();
                    }
                }
            }

            List<StarTGCropGene> newResourceGenome = geneMapToGenome(resourceGeneMap);
            List<StarTGCropGene> newProductionGenome = geneMapToGenome(productionGeneMap);
            List<StarTGCropGene> newAuxiliaryGenome = geneMapToGenome(auxiliaryGeneMap);

            ItemStack newGCrop = StarTGCropTraits.getCropWithTraits(newResourceGenome, newProductionGenome,
                    newAuxiliaryGenome, newClimateGenome);

            ItemStack firstCrop = foundCrops.get(0).copyWithCount(1);
            ItemStack secondCrop = foundCrops.get(1).copyWithCount(1);

            return StarTRecipeTypes.GCROP_BREEDER_RECIPES
                    .recipeBuilder("gcrop_crossbreeding")
                    .chancedInput(firstCrop, 10_00, 0)
                    .chancedInput(secondCrop, 10_00, 0)
                    .inputItems(new ItemStack(Items.SUGAR, 8))
                    .inputFluids(GTMaterials.Biomass.getFluid(2000))
                    .outputItems(newGCrop)
                    .duration(200)
                    .EUtV(GTValues.MV)
                    .buildRawRecipe();

        } else if (foundCrops.size() == 1) {
            ItemStack crop = foundCrops.get(0).copyWithCount(1);

            StarTGCropGenome cropStats = StarTGCropManager.gcropGenomeFromTag(crop);
            if (cropStats == null) return null;

            List<StarTGCropGene> newResourceGenome = filterGenesRandomly(cropStats.getResourceGenome(), 2);
            List<StarTGCropGene> newProductionGenome = filterGenesRandomly(cropStats.getProductionGenome(), 8);
            List<StarTGCropGene> newAuxiliaryGenome = filterGenesRandomly(cropStats.getAuxiliaryGenome(), 5);

            StarTGCropGene newClimateGenome = (cropStats.getClimateGene() != null &&
                    StarTCore.RNG.nextIntBetweenInclusive(1, 100) >= 5) ? cropStats.getClimateGene() : null;

            ItemStack newGCrop = StarTGCropTraits.getCropWithTraits(newResourceGenome, newProductionGenome,
                    newAuxiliaryGenome, newClimateGenome);

            return StarTRecipeTypes.GCROP_BREEDER_RECIPES
                    .recipeBuilder("gcrop_self_fertilization")
                    .chancedInput(crop, 10_00, 0)
                    .inputItems(new ItemStack(GTItems.FERTILIZER))
                    .inputFluids(GTMaterials.FermentedBiomass.getFluid(1000))
                    .outputItems(newGCrop)
                    .duration(200)
                    .EUtV(GTValues.MV)
                    .buildRawRecipe();
        }

        return null;
    }

    @Override
    public void buildRepresentativeRecipes() {
        ItemStack gCropInput = GCROP_MALFORMED.asStack();
        StarTCustomTooltipsManager.writeCustomTooltipsToItem(gCropInput.getOrCreateTag(),
                "behaviour.start_core.gcrop.random_crop");

        ItemStack gCropRandomSeed = GCROP_MALFORMED.asStack();
        StarTCustomTooltipsManager.writeCustomTooltipsToItem(gCropRandomSeed.getOrCreateTag(),
                "behaviour.start_core.gcrop.new_random_crop");

        gCropRandomSeed.setHoverName(Component.translatable(
                "behaviour.start_core.gcrop.random_crop_name"));

        GTRecipe crossBreedingRecipe = StarTRecipeTypes.GCROP_BREEDER_RECIPES
                .recipeBuilder("gcrop_crossbreeding")
                .chancedInput(gCropInput, 10_00, 0)
                .chancedInput(gCropInput, 10_00, 0)
                .inputItems(new ItemStack(Items.SUGAR, 8))
                .inputFluids(GTMaterials.Biomass.getFluid(2000))
                .outputItems(gCropRandomSeed)
                .duration(200)
                .EUtV(GTValues.MV)
                .buildRawRecipe();

        GTRecipe selfFertilizationRecipe = StarTRecipeTypes.GCROP_BREEDER_RECIPES
                .recipeBuilder("gcrop_self_fertilization")
                .chancedInput(gCropInput, 10_00, 0)
                .inputItems(new ItemStack(GTItems.FERTILIZER))
                .inputFluids(GTMaterials.FermentedBiomass.getFluid(1000))
                .outputItems(gCropRandomSeed)
                .duration(200)
                .EUtV(GTValues.MV)
                .buildRawRecipe();

        StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GCROP_BREEDER_RECIPES, "gcrops",
                crossBreedingRecipe);
        StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GCROP_BREEDER_RECIPES, "gcrops",
                selfFertilizationRecipe);
    }
}
