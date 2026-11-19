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
import com.startechnology.start_core.item.components.StarTGenomeHolderBehaviour;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.startechnology.start_core.item.gcrops.StarTGCropItems.*;

public class GenomeSeparatingLogic implements ICustomRecipeLogic {

    /**
     * Scans item handlers for a filled genome holder and an empty genome holder to construct a separation recipe.
     *
     * @param holder the recipe capability holder representing the machine
     * @return the constructed {@link GTRecipe}, or {@code null} if matching items are not present
     */
    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var itemHandlers = StarTCustomLogicUtils.getItemHandlers(holder);
        if (itemHandlers.isEmpty()) return null;

        List<ItemStack> allItems = StarTCustomLogicUtils.getAllItems(itemHandlers);

        return createGenomeSeparationRecipe(allItems);
    }

    /**
     * Splits the genes of a filled genome holder randomly across two output genome holders.
     *
     * @param itemSet the list of available input items
     * @return the constructed genome separation {@link GTRecipe}, or {@code null} if inputs are invalid
     */
    private GTRecipe createGenomeSeparationRecipe(List<ItemStack> itemSet) {
        ItemStack foundFilledHolder = null;
        ItemStack foundEmptyHolder = null;
        StarTGCropGenome gCropGenome = null;

        for (ItemStack item : itemSet) {
            if (item.isEmpty()) continue;

            if (foundFilledHolder == null) {
                if (StarTGenomeHolderBehaviour.getGenomeHolderBehaviour(item) != null) {
                    StarTGCropGenome genome = StarTGCropManager.gcropGenomeFromTag(item);
                    if (genome != null) {
                        foundFilledHolder = item;
                        gCropGenome = genome;
                        if (foundEmptyHolder != null) break;
                        continue;
                    }
                }
            }

            if (foundEmptyHolder == null && item.is(EMPTY_GENOME_HOLDER.get())) {
                foundEmptyHolder = item;
                if (foundFilledHolder != null) break;
            }
        }

        if (foundFilledHolder == null || foundEmptyHolder == null || gCropGenome == null) {
            return null;
        }

        List<StarTGCropGene> resourceGenome = gCropGenome.getResourceGenome();
        List<StarTGCropGene> productionGenome = gCropGenome.getProductionGenome();
        List<StarTGCropGene> auxiliaryGenome = gCropGenome.getAuxiliaryGenome();
        StarTGCropGene climateGene = gCropGenome.getClimateGene();

        List<StarTGCropGene> newResourceGenome1 = new ArrayList<>();
        List<StarTGCropGene> newResourceGenome2 = new ArrayList<>();
        List<StarTGCropGene> newProductionGenome1 = new ArrayList<>();
        List<StarTGCropGene> newProductionGenome2 = new ArrayList<>();
        List<StarTGCropGene> newAuxiliaryGenome1 = new ArrayList<>();
        List<StarTGCropGene> newAuxiliaryGenome2 = new ArrayList<>();

        int highestTier = 0;
        highestTier = Math.max(highestTier, separateGenes(resourceGenome, newResourceGenome1, newResourceGenome2));
        highestTier = Math.max(highestTier,
                separateGenes(productionGenome, newProductionGenome1, newProductionGenome2));
        highestTier = Math.max(highestTier, separateGenes(auxiliaryGenome, newAuxiliaryGenome1, newAuxiliaryGenome2));

        StarTGCropGene climate1 = null;
        StarTGCropGene climate2 = null;
        if (climateGene != null) {
            if (StarTCore.RNG.nextBoolean()) {
                climate1 = climateGene;
            } else {
                climate2 = climateGene;
            }
        }

        StarTGCropGenome newGenome1 = new StarTGCropGenome(newResourceGenome1, newProductionGenome1,
                newAuxiliaryGenome1, climate1);
        StarTGCropGenome newGenome2 = new StarTGCropGenome(newResourceGenome2, newProductionGenome2,
                newAuxiliaryGenome2, climate2);

        ItemStack newHolder1 = createResultHolder(newGenome1);
        ItemStack newHolder2 = createResultHolder(newGenome2);

        return StarTRecipeTypes.GENOME_SEPARATING
                .recipeBuilder("holder_separation")
                .inputItems(foundFilledHolder.copyWithCount(1), foundEmptyHolder.copyWithCount(1))
                .outputItems(newHolder1, newHolder2)
                .duration(120)
                .EUtVA(GTValues.MV + highestTier)
                .buildRawRecipe();
    }

    /**
     * Randomly distributes genes from a source list into one of two destination lists with equal probability,
     * tracking the highest trait tier among the separated genes.
     *
     * @param source the input list of genes
     * @param dest1  the first destination gene list
     * @param dest2  the second destination gene list
     * @return the maximum trait tier observed among the separated genes
     */
    private static int separateGenes(List<StarTGCropGene> source, List<StarTGCropGene> dest1,
                                     List<StarTGCropGene> dest2) {
        int maxTier = 0;
        for (StarTGCropGene gene : source) {
            if (StarTCore.RNG.nextBoolean()) {
                dest1.add(gene);
            } else {
                dest2.add(gene);
            }
            maxTier = Math.max(maxTier, gene.getTrait().tier());
        }
        return maxTier;
    }

    /**
     * Creates an appropriate genome holder item stack for a separated genome; returns an empty genome holder
     * if the resulting genome contains no genes, or a filled holder tagged with the genome otherwise.
     *
     * @param genome the genome result to encapsulate
     * @return an {@link ItemStack} of either an empty or filled genome holder
     */
    private static ItemStack createResultHolder(StarTGCropGenome genome) {
        if (genome.isEmpty()) {
            return EMPTY_GENOME_HOLDER.asStack();
        }
        ItemStack holder = FILLED_GENOME_HOLDER.asStack();
        StarTGCropManager.writeGCRopGenomeToItem(holder.getOrCreateTag(), genome);
        return holder;
    }

    @Override
    public void buildRepresentativeRecipes() {
        ItemStack randomHolder = FILLED_GENOME_HOLDER.asStack();
        ItemStack newHolder = FILLED_GENOME_HOLDER.asStack();
        ItemStack emptyHolder = EMPTY_GENOME_HOLDER.asStack();

        StarTCustomTooltipsManager.writeCustomTooltipsToItem(randomHolder.getOrCreateTag(),
                "behaviour.start_core.genome_holder.random_holder");
        StarTCustomTooltipsManager.writeCustomTooltipsToItem(newHolder.getOrCreateTag(),
                "behaviour.start_core.genome_holder.separated_holder");

        GTRecipe separationRecipe = StarTRecipeTypes.GENOME_SEPARATING
                .recipeBuilder("holder_separation")
                .inputItems(randomHolder, emptyHolder)
                .outputItems(newHolder, newHolder)
                .duration(120)
                .EUt(GTValues.V[GTValues.MV])
                .buildRawRecipe();

        StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GENOME_SEPARATING, "gcrops",
                separationRecipe);
    }
}
