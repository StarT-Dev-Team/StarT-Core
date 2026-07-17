package com.startechnology.start_core.recipe.logic.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.api.gcrop.*;
import com.startechnology.start_core.data.gcrops.StarTGCropTraits;
import com.startechnology.start_core.item.StarTGCropItems;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

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

        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack itemInSlot = handler.getStackInSlot(i);

            if (itemInSlot.isEmpty()) continue;

            StarTGCropPlant existingStats = StarTGCropManager.gcropGenomeFromTag(itemInSlot);

            if (existingStats == null) continue;

            foundCrops.add(itemInSlot);

            if (foundCrops.size() == 2) break;
        }

        if (foundCrops.size() < 2) return null;

        HashMap<String, Integer> resourceGeneMap = new HashMap<>();
        HashMap<String, Integer> productionGeneMap = new HashMap<>();
        HashMap<String, Integer> auxiliaryGeneMap = new HashMap<>();

        BiFunction<StarTGCropGene, HashMap<String, Integer>, Integer> handleGeneMeiosis = (gene, geneMap) -> {
            int alleles = gene.getDominantAlleles();
            StarTGCropTraits.StarTGCropTrait trait = gene.getTrait();
            String traitName = trait.name();
            int alleleCount = trait.alleleCount();

            int alleleAddition = 0;
            int finalAlleleCount;

            for (int i = 0; i < alleles; ++i) {
                if (StarTCore.RNG.nextIntBetweenInclusive(0, 1) == 1) alleleAddition++;
            }

            boolean traitExists = geneMap.containsKey(traitName);

            if (traitExists) {
                int currentCount = geneMap.get(traitName);
                finalAlleleCount = Math.max(currentCount + alleleAddition, alleleCount);
            } else finalAlleleCount = alleleAddition;

            return finalAlleleCount;
        };

        // Go over both crops
        for (ItemStack crop : foundCrops) {
            StarTGCropPlant cropStats = StarTGCropManager.gcropGenomeFromTag(crop);
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

        ItemEntry<ComponentItem> gCropItem = StarTGCropItems.getGCropByGenome(allResourceTraits);
        ItemStack newGCrop = (gCropItem == null) ? new ItemStack(GCROP_MALFORMED.get()) : gCropItem.asStack();

        StarTGCropPlant newGenome = new StarTGCropPlant(newResourceGenome, newProductionGenome,
                newAuxiliaryGenome);

        StarTGCropManager.writeGCRopGenomeToItem(newGCrop.getOrCreateTag(), newGenome);

        ItemStack firstItem = foundCrops.get(0).copyWithCount(1);
        StarTGCropPlant firstGenome = StarTGCropManager.gcropGenomeFromTag(foundCrops.get(0));
        StarTGCropManager.writeGCRopGenomeToItem(firstItem.getOrCreateTag(), firstGenome);

        ItemStack secondItem = foundCrops.get(0).copyWithCount(1);
        StarTGCropPlant secondGenome = StarTGCropManager.gcropGenomeFromTag(foundCrops.get(1));
        StarTGCropManager.writeGCRopGenomeToItem(secondItem.getOrCreateTag(), secondGenome);

        return StarTRecipeTypes.GCROP_BREEDER_RECIPES
                .recipeBuilder("gcrop_breeding")
                .inputItems(foundCrops.get(0).copyWithCount(1), foundCrops.get(1).copyWithCount(1))
                .outputItems(newGCrop)
                .inputFluids(GTMaterials.Water.getFluid(8000))
                .duration(200)
                .EUtV(GTValues.MV)
                .buildRawRecipe();
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

        GTRecipe AuxMutation0_3Recipe = StarTRecipeTypes.GCROP_BREEDER_RECIPES
                .recipeBuilder("aux_mutation_0_to_3")
                .inputItems(gCropInput.copyWithCount(1), gCropInput.copyWithCount(1))
                .outputItems(gCropRandomSeed)
                .inputFluids(GTMaterials.Water.getFluid(8000))
                .duration(200)
                .EUtV(GTValues.MV)
                .buildRawRecipe();

        StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GCROP_BREEDER_RECIPES, AuxMutation0_3Recipe);
    }
}
