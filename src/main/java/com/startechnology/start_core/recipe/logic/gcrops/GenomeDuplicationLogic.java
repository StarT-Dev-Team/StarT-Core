package com.startechnology.start_core.recipe.logic.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.api.gcrop.*;
import com.startechnology.start_core.item.components.StarTGenomeHolderBehaviour;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.startechnology.start_core.item.gcrops.StarTGCropItems.EMPTY_GENOME_HOLDER;
import static com.startechnology.start_core.item.gcrops.StarTGCropItems.FILLED_GENOME_HOLDER;

public class GenomeDuplicationLogic implements ICustomRecipeLogic {

    /**
     * Scans item handlers for input genome holders and builds a duplication recipe if valid inputs are present.
     *
     * @param holder the recipe capability holder representing the machine
     * @return the constructed duplication {@link GTRecipe}, or {@code null} if inputs do not match
     */
    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var itemHandlers = StarTCustomLogicUtils.getItemHandlers(holder);
        if (itemHandlers.isEmpty()) return null;

        List<ItemStack> allItems = StarTCustomLogicUtils.getAllItems(itemHandlers);

        return createGenomeDuplicationRecipe(allItems);
    }

    /**
     * Finds 1 filled genome holder and 3 empty genome holders in the item set to construct a genome duplication recipe.
     *
     * @param itemSet the list of available input items
     * @return the constructed {@link GTRecipe}, or {@code null} if inputs are insufficient
     */
    private GTRecipe createGenomeDuplicationRecipe(List<ItemStack> itemSet) {
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

        int highestTier = calculateHighestTier(gCropGenome);

        return StarTRecipeTypes.GENOME_GATHERING
                .recipeBuilder("holder_duplication")
                .inputItems(foundFilledHolder.copyWithCount(1), foundEmptyHolder.copyWithCount(3))
                .inputFluids(GTMaterials.Dimethylamine.getFluid(1000))
                .outputItems(foundFilledHolder.copyWithCount(4))
                .duration(120)
                .EUtVA(GTValues.MV + highestTier)
                .buildRawRecipe();
    }

    /**
     * Calculates the highest trait tier present across all resource, production, and auxiliary genes in a genome.
     *
     * @param genome the genome to inspect
     * @return the maximum tier found among the genome's traits, or 0 if empty
     */
    public static int calculateHighestTier(StarTGCropGenome genome) {
        int highestTier = 0;
        for (StarTGCropGene gene : genome.getResourceGenome()) {
            highestTier = Math.max(highestTier, gene.getTrait().tier());
        }
        for (StarTGCropGene gene : genome.getProductionGenome()) {
            highestTier = Math.max(highestTier, gene.getTrait().tier());
        }
        for (StarTGCropGene gene : genome.getAuxiliaryGenome()) {
            highestTier = Math.max(highestTier, gene.getTrait().tier());
        }
        return highestTier;
    }

    @Override
    public void buildRepresentativeRecipes() {
        ItemStack genomeHolder = FILLED_GENOME_HOLDER.asStack();
        ItemStack newGenomeHolder = FILLED_GENOME_HOLDER.asStack(4);
        ItemStack emptyHolder = EMPTY_GENOME_HOLDER.asStack(3);

        StarTCustomTooltipsManager.writeCustomTooltipsToItem(genomeHolder.getOrCreateTag(),
                "behaviour.start_core.genome_holder.random_holder");
        StarTCustomTooltipsManager.writeCustomTooltipsToItem(newGenomeHolder.getOrCreateTag(),
                "behaviour.start_core.genome_holder.copied_holder");

        GTRecipe duplicationRecipe = StarTRecipeTypes.GENOME_GATHERING
                .recipeBuilder("holder_duplication")
                .inputItems(genomeHolder, emptyHolder)
                .inputFluids(GTMaterials.Dimethylamine.getFluid(1000))
                .outputItems(newGenomeHolder)
                .duration(120)
                .EUt(GTValues.V[GTValues.MV])
                .buildRawRecipe();

        StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GENOME_GATHERING, "gcrops",
                duplicationRecipe);
    }
}
