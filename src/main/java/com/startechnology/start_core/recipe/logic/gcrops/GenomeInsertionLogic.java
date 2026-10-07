package com.startechnology.start_core.recipe.logic.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.api.gcrop.StarTGCropGene;
import com.startechnology.start_core.api.gcrop.StarTGCropGenome;
import com.startechnology.start_core.api.gcrop.StarTGCropManager;
import com.startechnology.start_core.api.gcrop.StarTGCropTraits;
import com.startechnology.start_core.item.components.StarTGCropBehaviour;
import com.startechnology.start_core.item.components.StarTGenomeHolderBehaviour;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.startechnology.start_core.item.gcrops.StarTGCropItems.EMPTY_GENOME_HOLDER;
import static com.startechnology.start_core.item.gcrops.StarTGCropItems.FILLED_GENOME_HOLDER;
import static com.startechnology.start_core.item.gcrops.StarTGCropItems.GCROP_MALFORMED;

public class GenomeInsertionLogic implements ICustomRecipeLogic {

    /**
     * Scans item handlers for a filled genome holder and a GCrop item to create a genome insertion recipe.
     *
     * @param holder the recipe capability holder representing the machine
     * @return the constructed {@link GTRecipe}, or {@code null} if matching items are not present
     */
    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var itemHandlers = StarTCustomLogicUtils.getItemHandlers(holder);
        if (itemHandlers.isEmpty()) return null;

        List<ItemStack> allItems = StarTCustomLogicUtils.getAllItems(itemHandlers);

        return createGenomeInsertionRecipe(allItems);
    }

    /**
     * Inserts the genome stored in a filled genome holder into a GCrop, returning the updated crop
     * and an empty genome holder.
     *
     * @param itemSet the list of available input items
     * @return the constructed genome insertion {@link GTRecipe}, or {@code null} if inputs are invalid
     */
    private GTRecipe createGenomeInsertionRecipe(List<ItemStack> itemSet) {
        ItemStack foundHolder = null;
        ItemStack foundGCrop = null;
        StarTGCropGenome gCropGenome = null;

        for (ItemStack item : itemSet) {
            if (item.isEmpty()) continue;

            if (foundGCrop == null && StarTGCropBehaviour.getGCropBehaviour(item) != null) {
                foundGCrop = item;
                if (foundHolder != null) break;
                continue;
            }

            if (foundHolder == null && StarTGenomeHolderBehaviour.getGenomeHolderBehaviour(item) != null) {
                StarTGCropGenome genome = StarTGCropManager.gcropGenomeFromTag(item);
                if (genome != null) {
                    foundHolder = item;
                    gCropGenome = genome;
                    if (foundGCrop != null) break;
                }
            }
        }

        if (foundHolder == null || foundGCrop == null || gCropGenome == null) return null;

        ItemStack emptyHolder = EMPTY_GENOME_HOLDER.asStack();

        List<StarTGCropGene> existingResourceGenome = gCropGenome.getResourceGenome();
        List<StarTGCropGene> existingProductionGenome = gCropGenome.getProductionGenome();
        List<StarTGCropGene> existingAuxiliaryGenome = gCropGenome.getAuxiliaryGenome();
        StarTGCropGene existingClimateGenome = gCropGenome.getClimateGene();

        ItemStack newGCrop = StarTGCropTraits.getCropWithTraits(existingResourceGenome, existingProductionGenome,
                existingAuxiliaryGenome, existingClimateGenome);

        StarTGCropBehaviour newCropBehaviour = StarTGCropBehaviour.getGCropBehaviour(newGCrop);
        if (newCropBehaviour == null) return null;

        int cropTier = newCropBehaviour.getCropTier();

        return StarTRecipeTypes.GENOME_INSERTION
                .recipeBuilder("holder_insertion")
                .inputItems(foundHolder.copyWithCount(1), foundGCrop.copyWithCount(1))
                .outputItems(newGCrop, emptyHolder)
                .duration(120)
                .EUtVA(GTValues.MV + cropTier)
                .buildRawRecipe();
    }

    @Override
    public void buildRepresentativeRecipes() {
        ItemStack filledGenomeHolder = FILLED_GENOME_HOLDER.asStack();
        ItemStack emptyGenomeHolder = EMPTY_GENOME_HOLDER.asStack();
        ItemStack gCrop = GCROP_MALFORMED.asStack();
        ItemStack newGCrop = GCROP_MALFORMED.asStack();

        StarTCustomTooltipsManager.writeCustomTooltipsToItem(gCrop.getOrCreateTag(),
                "behaviour.start_core.gcrop.random_crop");
        StarTCustomTooltipsManager.writeCustomTooltipsToItem(newGCrop.getOrCreateTag(),
                "behaviour.start_core.gcrop.inserted_gcrop");

        StarTCustomTooltipsManager.writeCustomTooltipsToItem(filledGenomeHolder.getOrCreateTag(),
                "behaviour.start_core.genome_holder.random_holder");

        GTRecipe insertionRecipe = StarTRecipeTypes.GENOME_INSERTION
                .recipeBuilder("holder_insertion")
                .inputItems(filledGenomeHolder, gCrop)
                .outputItems(emptyGenomeHolder, newGCrop)
                .duration(120)
                .EUt(GTValues.V[GTValues.MV])
                .buildRawRecipe();

        StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GENOME_INSERTION, "gcrops",
                insertionRecipe);
    }
}
