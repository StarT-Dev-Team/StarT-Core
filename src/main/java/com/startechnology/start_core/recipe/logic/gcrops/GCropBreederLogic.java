package com.startechnology.start_core.recipe.logic.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
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
        var handlers = StarTCustomLogicUtils.getItemHandlers(holder);

        return StarTCustomLogicUtils.createCustomlogicRecipeWithItemHandlers(handlers, this::createBacteriaRecipe);
    }

    private List<StarTGCropGene> geneMapToGenome(HashMap<String, Integer> geneMap) {
        List<StarTGCropGene> newGenome = new ArrayList<>();

        for (String traitName : geneMap.keySet()) {
            int traitCount = geneMap.get(traitName);
            var trait = StarTGCropTraits.getTrait(traitName);
            newGenome.add(new StarTGCropGene(trait, traitCount));
        }

        return newGenome;
    }

    private Integer handleGeneMeiosis(StarTGCropGene gene, HashMap<String, Integer> geneMap) {
        int alleles = gene.getDominantAlleles();
        StarTGCropTrait trait = gene.getTrait();
        String traitId = trait.id();
        int maxAlleleCount = trait.alleleCount();

        int alleleAddition = 0;
        int finalAlleleCount;

        for (int i = 0; i < alleles; i++) {
            if (StarTCore.RNG.nextIntBetweenInclusive(0, 1) == 1 &&
                    (double) alleleAddition < (double) maxAlleleCount / 2)
                alleleAddition++;
        }

        boolean traitExists = geneMap.containsKey(traitId);

        if (traitExists) {
            int currentCount = geneMap.get(traitId);
            finalAlleleCount = Math.min(currentCount + alleleAddition, maxAlleleCount);
        } else finalAlleleCount = alleleAddition;

        return finalAlleleCount;
    }

    private GTRecipe createBacteriaRecipe(NotifiableItemStackHandler handler) {
        List<ItemStack> foundCrops = new ArrayList<>();

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack itemInSlot = handler.getStackInSlot(i);

            if (itemInSlot.isEmpty()) continue;

            StarTGCropBehaviour cropBehaviour = StarTGCropBehaviour.getGCropBehaviour(itemInSlot);
            if (cropBehaviour == null) continue;

            StarTGCropGenome existingStats = StarTGCropManager.gcropGenomeFromTag(itemInSlot);
            if (existingStats == null) continue;

            foundCrops.add(itemInSlot);

            if (foundCrops.size() == 2) break;
        }

        if (foundCrops.size() == 2) {
            HashMap<String, Integer> resourceGeneMap = new HashMap<>();
            HashMap<String, Integer> productionGeneMap = new HashMap<>();
            HashMap<String, Integer> auxiliaryGeneMap = new HashMap<>();

            StarTGCropGene newClimateGenome = null;

            // Go over both crops
            for (ItemStack crop : foundCrops) {
                StarTGCropGenome cropStats = StarTGCropManager.gcropGenomeFromTag(crop);

                if (cropStats == null) continue;

                // Apply Meiosis mimicry to all traits for harvesting the new traits
                for (StarTGCropGene existingGene : cropStats.getResourceGenome()) {
                    StarTGCropTrait trait = existingGene.getTrait();
                    String traitId = trait.id();

                    int newAlleleCount = handleGeneMeiosis(existingGene, resourceGeneMap);

                    if (newAlleleCount != 0) resourceGeneMap.put(traitId, newAlleleCount);
                }

                for (StarTGCropGene existingGene : cropStats.getProductionGenome()) {
                    StarTGCropTrait trait = existingGene.getTrait();
                    String traitId = trait.id();

                    int newAlleleCount = handleGeneMeiosis(existingGene, productionGeneMap);

                    if (newAlleleCount != 0) productionGeneMap.put(traitId, newAlleleCount);
                }

                for (StarTGCropGene existingGene : cropStats.getAuxiliaryGenome()) {
                    StarTGCropTrait trait = existingGene.getTrait();
                    String traitId = trait.id();

                    int newAlleleCount = handleGeneMeiosis(existingGene, auxiliaryGeneMap);

                    if (newAlleleCount != 0) auxiliaryGeneMap.put(traitId, newAlleleCount);
                }

                if (newClimateGenome != null) continue;

                StarTGCropGene existingClimateGenome = cropStats.getClimateGene();
                if (StarTCore.RNG.nextIntBetweenInclusive(0, 1) == 1) newClimateGenome = existingClimateGenome;
            }

            // Compose Genomes for all traits
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
            assert cropStats != null;

            List<StarTGCropGene> existingResourceGenome = cropStats.getResourceGenome();
            List<StarTGCropGene> existingProductionGenome = cropStats.getProductionGenome();
            List<StarTGCropGene> existingAuxiliaryGenome = cropStats.getAuxiliaryGenome();
            StarTGCropGene existingClimateGenome = cropStats.getClimateGene();

            List<StarTGCropGene> newResourceGenome = new ArrayList<>();
            List<StarTGCropGene> newProductionGenome = new ArrayList<>();
            List<StarTGCropGene> newAuxiliaryGenome = new ArrayList<>();
            StarTGCropGene newClimateGenome = null;

            for (StarTGCropGene gene : existingResourceGenome) {
                if (StarTCore.RNG.nextIntBetweenInclusive(1, 100) >= 2) newResourceGenome.add(gene);
            }

            for (StarTGCropGene gene : existingProductionGenome) {
                if (StarTCore.RNG.nextIntBetweenInclusive(1, 100) >= 8) newProductionGenome.add(gene);
            }

            for (StarTGCropGene gene : existingAuxiliaryGenome) {
                if (StarTCore.RNG.nextIntBetweenInclusive(1, 100) >= 5) newAuxiliaryGenome.add(gene);
            }

            if (StarTCore.RNG.nextIntBetweenInclusive(1, 100) >= 5) newClimateGenome = existingClimateGenome;

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
        } else return null;
    }

    @Override
    public void buildRepresentativeRecipes() {
        ItemStack gCropInput = new ItemStack(GCROP_MALFORMED.get());
        StarTCustomTooltipsManager.writeCustomTooltipsToItem(gCropInput.getOrCreateTag(),
                "behaviour.start_core.gcrop.random_crop");

        ItemStack gCropRandomSeed = new ItemStack(GCROP_MALFORMED.asItem());
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
                .inputFluids(GTMaterials.Biomass.getFluid(1000))
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
