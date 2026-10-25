package com.startechnology.start_core.recipe.logic.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.api.gcrop.StarTGCropGene;
import com.startechnology.start_core.api.gcrop.StarTGCropGenome;
import com.startechnology.start_core.api.gcrop.StarTGCropManager;
import com.startechnology.start_core.api.gcrop.StarTGCropTrait;
import com.startechnology.start_core.item.components.StarTGenomeHolderBehaviour;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.startechnology.start_core.item.gcrops.StarTGCropItems.EMPTY_GENOME_HOLDER;
import static com.startechnology.start_core.item.gcrops.StarTGCropItems.FILLED_GENOME_HOLDER;

public class GenomeMixingLogic implements ICustomRecipeLogic {

    /**
     * Scans item handlers for two filled genome holders to construct a genome mixing recipe.
     *
     * @param holder the recipe capability holder representing the machine
     * @return the constructed {@link GTRecipe}, or {@code null} if matching holders are not found
     */
    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var itemHandlers = StarTCustomLogicUtils.getItemHandlers(holder);
        if (itemHandlers.isEmpty()) return null;

        List<ItemStack> allItems = StarTCustomLogicUtils.getAllItems(itemHandlers);

        return createGenomeMixingRecipe(allItems);
    }

    /**
     * Converts a map of traits and combined allele counts into a list of {@link StarTGCropGene} instances.
     *
     * @param geneMap the trait-to-allele count map
     * @return the newly constructed list of genes
     */
    private static List<StarTGCropGene> geneMapToGenome(Map<StarTGCropTrait, Integer> geneMap) {
        List<StarTGCropGene> newGenome = new ArrayList<>(geneMap.size());
        for (Map.Entry<StarTGCropTrait, Integer> entry : geneMap.entrySet()) {
            newGenome.add(new StarTGCropGene(entry.getKey(), entry.getValue()));
        }
        return newGenome;
    }

    /**
     * Merges a list of genes into an accumulator trait map, clamping alleles to the trait's maximum,
     * and tracks the highest trait tier encountered.
     *
     * @param genes      the list of genes to merge
     * @param storageMap the accumulator map for traits and allele counts
     * @return the highest trait tier found in the input gene list
     */
    private static int mergeGenes(List<StarTGCropGene> genes, Map<StarTGCropTrait, Integer> storageMap) {
        int maxTier = 0;
        for (StarTGCropGene gene : genes) {
            StarTGCropTrait trait = gene.getTrait();
            maxTier = Math.max(maxTier, trait.tier());
            storageMap.merge(trait, gene.getDominantAlleles(),
                    (current, added) -> Math.min(current + added, trait.alleleCount()));
        }
        return maxTier;
    }

    /**
     * Combines the genetic traits of two filled genome holders into a single newly mixed genome holder,
     * producing an empty genome holder as a byproduct.
     *
     * @param itemSet the list of available input items
     * @return the constructed genome mixing {@link GTRecipe}, or {@code null} if two valid holders are not found
     */
    private GTRecipe createGenomeMixingRecipe(List<ItemStack> itemSet) {
        ItemStack foundFirstHolder = null;
        ItemStack foundSecondHolder = null;
        StarTGCropGenome firstGenome = null;
        StarTGCropGenome secondGenome = null;

        for (ItemStack item : itemSet) {
            if (item.isEmpty()) continue;

            if (StarTGenomeHolderBehaviour.getGenomeHolderBehaviour(item) != null) {
                StarTGCropGenome genome = StarTGCropManager.gcropGenomeFromTag(item);
                if (genome != null) {
                    if (foundFirstHolder == null) {
                        foundFirstHolder = item;
                        firstGenome = genome;
                    } else {
                        foundSecondHolder = item;
                        secondGenome = genome;
                        break;
                    }
                }
            }
        }

        if (foundFirstHolder == null || foundSecondHolder == null || firstGenome == null || secondGenome == null) {
            return null;
        }

        int highestTier = 0;
        Map<StarTGCropTrait, Integer> resourceMap = new HashMap<>();
        Map<StarTGCropTrait, Integer> productionMap = new HashMap<>();
        Map<StarTGCropTrait, Integer> auxiliaryMap = new HashMap<>();

        highestTier = Math.max(highestTier, mergeGenes(firstGenome.getResourceGenome(), resourceMap));
        highestTier = Math.max(highestTier, mergeGenes(firstGenome.getProductionGenome(), productionMap));
        highestTier = Math.max(highestTier, mergeGenes(firstGenome.getAuxiliaryGenome(), auxiliaryMap));

        highestTier = Math.max(highestTier, mergeGenes(secondGenome.getResourceGenome(), resourceMap));
        highestTier = Math.max(highestTier, mergeGenes(secondGenome.getProductionGenome(), productionMap));
        highestTier = Math.max(highestTier, mergeGenes(secondGenome.getAuxiliaryGenome(), auxiliaryMap));

        StarTGCropGene climate1 = firstGenome.getClimateGene();
        StarTGCropGene climate2 = secondGenome.getClimateGene();
        StarTGCropGene climateGene;
        if (climate1 == null) {
            climateGene = climate2;
        } else if (climate2 == null) {
            climateGene = climate1;
        } else {
            climateGene = StarTCore.RNG.nextBoolean() ? climate1 : climate2;
        }

        List<StarTGCropGene> newResourceGenome = geneMapToGenome(resourceMap);
        List<StarTGCropGene> newProductionGenome = geneMapToGenome(productionMap);
        List<StarTGCropGene> newAuxiliaryGenome = geneMapToGenome(auxiliaryMap);

        ItemStack newHolder = FILLED_GENOME_HOLDER.asStack();
        ItemStack emptyHolder = EMPTY_GENOME_HOLDER.asStack();

        StarTGCropGenome newGenome = new StarTGCropGenome(newResourceGenome, newProductionGenome, newAuxiliaryGenome,
                climateGene);
        StarTGCropManager.writeGCRopGenomeToItem(newHolder.getOrCreateTag(), newGenome);

        return StarTRecipeTypes.GENOME_MIXING
                .recipeBuilder("holder_mixing")
                .inputItems(foundFirstHolder.copyWithCount(1), foundSecondHolder.copyWithCount(1))
                .outputItems(newHolder, emptyHolder)
                .duration(120)
                .EUtVA(GTValues.MV + highestTier)
                .buildRawRecipe();
    }

    @Override
    public void buildRepresentativeRecipes() {
        ItemStack randomHolder = FILLED_GENOME_HOLDER.asStack();
        ItemStack newHolder = FILLED_GENOME_HOLDER.asStack();
        ItemStack emptyHolder = EMPTY_GENOME_HOLDER.asStack();

        StarTCustomTooltipsManager.writeCustomTooltipsToItem(randomHolder.getOrCreateTag(),
                "behaviour.start_core.genome_holder.random_holder");
        StarTCustomTooltipsManager.writeCustomTooltipsToItem(newHolder.getOrCreateTag(),
                "behaviour.start_core.genome_holder.combined_holder");

        GTRecipe mixingRecipe = StarTRecipeTypes.GENOME_MIXING
                .recipeBuilder("holder_mixing")
                .inputItems(randomHolder, randomHolder)
                .outputItems(newHolder, emptyHolder)
                .duration(120)
                .EUt(GTValues.V[GTValues.MV])
                .buildRawRecipe();

        StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GENOME_MIXING, "gcrops",
                mixingRecipe);
    }
}
