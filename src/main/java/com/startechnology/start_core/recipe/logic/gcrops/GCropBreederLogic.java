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
import com.startechnology.start_core.data.gcrops.StarTGCropTraits;
import com.startechnology.start_core.item.components.StarTGCropBehaviour;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;
import java.util.function.BiFunction;

import static com.startechnology.start_core.item.StarTGCropItems.GCROP_MALFORMED;

public class GCropBreederLogic implements GTRecipeType.ICustomRecipeLogic {

    @Override
    public GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var handlers = StarTCustomLogicUtils.getItemHandlers(holder);

        return StarTCustomLogicUtils.createCustomlogicRecipeWithItemHandlers(handlers, this::createBacteriaRecipe);
    }

    private GTRecipe createBacteriaRecipe(NotifiableItemStackHandler handler) {
        List<ItemStack> foundCrops = new ArrayList<>();

        for (int i = 1; i < handler.getSlots(); i++) {
            ItemStack itemInSlot = handler.getStackInSlot(i);

            if (itemInSlot.isEmpty()) continue;

            StarTGCropBehaviour cropBehaviour = StarTGCropBehaviour.getGCropBehaviour(itemInSlot);
            if (cropBehaviour == null) continue;

            StarTGCropGenome existingStats = StarTGCropManager.gcropGenomeFromTag(itemInSlot);
            if (existingStats == null) continue;

            foundCrops.add(itemInSlot);

            if (foundCrops.size() == 2) break;
        }

        BiFunction<StarTGCropGene, HashMap<String, Integer>, Integer> handleGeneMeiosis = (gene, geneMap) -> {
            int alleles = gene.getDominantAlleles();
            StarTGCropTraits.StarTGCropTrait trait = gene.getTrait();
            String traitName = trait.name();
            int maxAlleleCount = trait.alleleCount();

            int alleleAddition = 0;
            int finalAlleleCount;

            for (int i = 0; i < alleles; i++) {
                if (StarTCore.RNG.nextIntBetweenInclusive(0, 1) == 1 &&
                        (double) alleleAddition < (double) maxAlleleCount / 2)
                    alleleAddition++;
            }

            boolean traitExists = geneMap.containsKey(traitName);

            if (traitExists) {
                int currentCount = geneMap.get(traitName);
                finalAlleleCount = Math.max(currentCount + alleleAddition, maxAlleleCount);
            } else finalAlleleCount = alleleAddition;

            return finalAlleleCount;
        };

        if (foundCrops.size() == 2) {
            HashMap<String, Integer> resourceGeneMap = new HashMap<>();
            HashMap<String, Integer> productionGeneMap = new HashMap<>();
            HashMap<String, Integer> auxiliaryGeneMap = new HashMap<>();

            // Go over both crops
            for (ItemStack crop : foundCrops) {
                StarTGCropGenome cropStats = StarTGCropManager.gcropGenomeFromTag(crop);
                assert cropStats != null;

                // Apply Meiosis mimicry to all traits for harvesting the new traits
                for (StarTGCropGene existingGene : cropStats.getResourceGenome()) {
                    StarTGCropTraits.StarTGCropTrait trait = existingGene.getTrait();
                    String traitName = trait.name();

                    int newAlleleCount = handleGeneMeiosis.apply(existingGene, resourceGeneMap);

                    if (newAlleleCount != 0) resourceGeneMap.put(traitName, newAlleleCount);
                }

                for (StarTGCropGene existingGene : cropStats.getProductionGenome()) {
                    StarTGCropTraits.StarTGCropTrait trait = existingGene.getTrait();
                    String traitName = trait.name();

                    int newAlleleCount = handleGeneMeiosis.apply(existingGene, productionGeneMap);

                    if (newAlleleCount != 0) productionGeneMap.put(traitName, newAlleleCount);
                }

                for (StarTGCropGene existingGene : cropStats.getAuxiliaryGenome()) {
                    StarTGCropTraits.StarTGCropTrait trait = existingGene.getTrait();
                    String traitName = trait.name();

                    int newAlleleCount = handleGeneMeiosis.apply(existingGene, auxiliaryGeneMap);

                    if (newAlleleCount != 0) auxiliaryGeneMap.put(traitName, newAlleleCount);
                }

            }

            List<StarTGCropGene> newResourceGenome = new ArrayList<>();
            List<StarTGCropGene> newProductionGenome = new ArrayList<>();
            List<StarTGCropGene> newAuxiliaryGenome = new ArrayList<>();

            List<StarTGCropTraits.StarTGCropTrait> allResourceTraits = new ArrayList<>();

            // Compose Genomes for all traits
            for (String traitName : resourceGeneMap.keySet()) {
                int traitCount = resourceGeneMap.get(traitName);
                var trait = StarTGCropTraits.getTrait(traitName);
                newResourceGenome.add(new StarTGCropGene(trait, traitCount));
                allResourceTraits.add(trait);
            }

            for (String traitName : productionGeneMap.keySet()) {
                int traitCount = productionGeneMap.get(traitName);
                var trait = StarTGCropTraits.getTrait(traitName);
                newProductionGenome.add(new StarTGCropGene(trait, traitCount));
            }

            for (String traitName : auxiliaryGeneMap.keySet()) {
                int traitCount = auxiliaryGeneMap.get(traitName);
                var trait = StarTGCropTraits.getTrait(traitName);
                newAuxiliaryGenome.add(new StarTGCropGene(trait, traitCount));
            }

            ItemStack newGCrop = StarTGCropTraits.getCropWithTraits(newResourceGenome, newProductionGenome,
                    newAuxiliaryGenome);

            ItemStack firstCrop = foundCrops.get(0).copyWithCount(1);
            StarTGCropGenome firstGenome = StarTGCropManager.gcropGenomeFromTag(foundCrops.get(0));
            assert firstGenome != null;
            StarTGCropManager.writeGCRopGenomeToItem(firstCrop.getOrCreateTag(), firstGenome);

            ItemStack secondCrop = foundCrops.get(1).copyWithCount(1);
            StarTGCropGenome secondGenome = StarTGCropManager.gcropGenomeFromTag(foundCrops.get(1));
            assert secondGenome != null;
            StarTGCropManager.writeGCRopGenomeToItem(secondCrop.getOrCreateTag(), secondGenome);

            return StarTRecipeTypes.GCROP_BREEDER_RECIPES
                    .recipeBuilder("gcrop_crossbreeding")
                    .chancedInput(firstCrop, 10_00, 0)
                    .chancedInput(secondCrop, 10_00, 0)
                    .inputItems(new ItemStack(GTItems.FERTILIZER).copyWithCount(1))
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

            List<StarTGCropGene> newResourceGenome = new ArrayList<>();
            List<StarTGCropGene> newProductionGenome = new ArrayList<>();
            List<StarTGCropGene> newAuxiliaryGenome = new ArrayList<>();

            for (StarTGCropGene gene : existingResourceGenome) {
                if (StarTCore.RNG.nextIntBetweenInclusive(1, 100) >= 2) newResourceGenome.add(gene);
            }

            for (StarTGCropGene gene : existingProductionGenome) {
                if (StarTCore.RNG.nextIntBetweenInclusive(1, 100) >= 8) newProductionGenome.add(gene);
            }

            for (StarTGCropGene gene : existingAuxiliaryGenome) {
                if (StarTCore.RNG.nextIntBetweenInclusive(1, 100) >= 5) newAuxiliaryGenome.add(gene);
            }

            ItemStack newGCrop = StarTGCropTraits.getCropWithTraits(newResourceGenome, newProductionGenome,
                    newAuxiliaryGenome);

            return StarTRecipeTypes.GCROP_BREEDER_RECIPES
                    .recipeBuilder("gcrop_self_fertilization")
                    .chancedInput(crop, 10_00, 0)
                    .inputItems(new ItemStack(GTItems.FERTILIZER).copyWithCount(1))
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
                "behaviour.start_core.bacteria.input");

        ItemStack gCropRandomSeed = new ItemStack(GCROP_MALFORMED.asItem());
        StarTCustomTooltipsManager.writeCustomTooltipsToItem(gCropRandomSeed.getOrCreateTag(),
                "behaviour.start_core.gcrop.random_crop");

        gCropRandomSeed.setHoverName(Component.translatable(
                "behaviour.start_core.gcrop.random_crop_name"));

        GTRecipe crossBreedingRecipe = StarTRecipeTypes.GCROP_BREEDER_RECIPES
                .recipeBuilder("gcrop_crossbreeding")
                .chancedInput(gCropInput.copyWithCount(1), 10_00, 0)
                .chancedInput(gCropInput.copyWithCount(1), 10_00, 0)
                .inputItems(new ItemStack(Items.SUGAR).copyWithCount(8))
                .inputFluids(GTMaterials.Biomass.getFluid(2000))
                .outputItems(gCropRandomSeed)
                .duration(200)
                .EUtV(GTValues.MV)
                .buildRawRecipe();

        GTRecipe selfFertilizationRecipe = StarTRecipeTypes.GCROP_BREEDER_RECIPES
                .recipeBuilder("gcrop_self_fertilization")
                .chancedInput(gCropInput.copyWithCount(1), 10_00, 0)
                .inputItems(new ItemStack(GTItems.FERTILIZER).copyWithCount(1))
                .inputFluids(GTMaterials.Biomass.getFluid(2000))
                .outputItems(gCropRandomSeed)
                .duration(200)
                .EUtV(GTValues.MV)
                .buildRawRecipe();

        StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GCROP_BREEDER_RECIPES, crossBreedingRecipe);
        StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GCROP_BREEDER_RECIPES,
                selfFertilizationRecipe);
    }
}
