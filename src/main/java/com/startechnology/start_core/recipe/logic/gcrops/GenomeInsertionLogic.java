package com.startechnology.start_core.recipe.logic.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.api.gcrop.StarTGCropGene;
import com.startechnology.start_core.api.gcrop.StarTGCropGenome;
import com.startechnology.start_core.api.gcrop.StarTGCropManager;
import com.startechnology.start_core.data.gcrops.StarTGCropTraits;
import com.startechnology.start_core.item.components.StarTGCropBehaviour;
import com.startechnology.start_core.item.components.StarTGenomeHolderBehaviour;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.startechnology.start_core.item.StarTGCropItems.*;

public class GenomeInsertionLogic implements ICustomRecipeLogic {

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var itemHandlers = StarTCustomLogicUtils.getItemHandlers(holder);
        if (itemHandlers.isEmpty()) return null;

        List<ItemStack> allItems = StarTCustomLogicUtils.getAllItems(itemHandlers);

        return createGenomeInsertionRecipe(allItems);
    }

    private GTRecipe createGenomeInsertionRecipe(List<ItemStack> itemSet) {
        ItemStack foundHolder = null;
        ItemStack foundGCrop = null;

        for (ItemStack item : itemSet) {
            if (StarTGCropBehaviour.getGCropBehaviour(item) != null) {
                foundGCrop = item;
            } else if (StarTGenomeHolderBehaviour.getGenomeHolderBehaviour(item) != null) {
                foundHolder = item;
            }
        }

        if (foundHolder == null || foundGCrop == null) return null;

        StarTGenomeHolderBehaviour holderBehaviour = StarTGenomeHolderBehaviour.getGenomeHolderBehaviour(foundHolder);
        if (holderBehaviour == null) return null;

        StarTGCropGenome gCropGenome = StarTGCropManager.gcropGenomeFromTag(foundHolder);
        if (gCropGenome == null) return null;

        StarTGCropBehaviour cropBehaviour = StarTGCropBehaviour.getGCropBehaviour(foundGCrop);
        if (cropBehaviour == null) return null;

        ItemStack emptyHolder = new ItemStack(EMPTY_GENOME_HOLDER.asItem());

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
        ItemStack filledGenomeHolder = new ItemStack(FILLED_GENOME_HOLDER.asItem());
        ItemStack emptyGenomeHolder = new ItemStack(EMPTY_GENOME_HOLDER.asItem());
        ItemStack gCrop = new ItemStack(GCROP_MALFORMED.asItem());
        ItemStack newGCrop = new ItemStack(GCROP_MALFORMED.asItem());

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
