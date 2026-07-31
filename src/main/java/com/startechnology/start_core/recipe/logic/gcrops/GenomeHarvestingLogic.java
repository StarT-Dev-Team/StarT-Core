package com.startechnology.start_core.recipe.logic.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.api.gcrop.StarTGCropGenome;
import com.startechnology.start_core.api.gcrop.StarTGCropManager;
import com.startechnology.start_core.item.components.StarTGCropBehaviour;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.startechnology.start_core.item.StarTGCropItems.*;

public class GenomeHarvestingLogic implements ICustomRecipeLogic {

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var itemHandlers = StarTCustomLogicUtils.getItemHandlers(holder);
        if (itemHandlers.isEmpty()) return null;

        List<ItemStack> allItems = StarTCustomLogicUtils.getAllItems(itemHandlers);

        return createGenomeHarvestRecipe(allItems);
    }

    private GTRecipe createGenomeHarvestRecipe(List<ItemStack> itemSet) {
        ItemStack foundHolder = null;
        ItemStack foundGCrop = null;

        for (ItemStack item : itemSet) {
            if (StarTGCropBehaviour.getGCropBehaviour(item) != null) {
                foundGCrop = item;
            } else if (item.getItem().equals(EMPTY_GENOME_HOLDER.asItem())) {
                foundHolder = item;
            }
        }

        if (foundHolder == null || foundGCrop == null) return null;

        StarTGCropGenome gCropGenome = StarTGCropManager.gcropGenomeFromTag(foundGCrop);
        if (gCropGenome == null) return null;

        StarTGCropBehaviour cropBehaviour = StarTGCropBehaviour.getGCropBehaviour(foundGCrop);
        if (cropBehaviour == null) return null;

        int cropTier = cropBehaviour.getCropTier();

        ItemStack newHolder = new ItemStack(FILLED_GENOME_HOLDER);

        StarTGCropManager.writeGCRopGenomeToItem(newHolder.getOrCreateTag(), gCropGenome);

        return StarTRecipeTypes.GENOME_GATHERING
                .recipeBuilder("holder_harvesting")
                .inputItems(foundHolder.copyWithCount(1), foundGCrop.copyWithCount(1))
                .outputItems(newHolder)
                .duration(120)
                .EUtVA(GTValues.MV + cropTier)
                .buildRawRecipe();
    }

    @Override
    public void buildRepresentativeRecipes() {
        ItemStack genomeHolderEmpty = new ItemStack(EMPTY_GENOME_HOLDER.asItem());
        ItemStack genomeHolderFilled = new ItemStack(FILLED_GENOME_HOLDER.asItem());
        ItemStack gCrop = new ItemStack(GCROP_MALFORMED.asItem());

        StarTCustomTooltipsManager.writeCustomTooltipsToItem(gCrop.getOrCreateTag(),
                "behaviour.start_core.gcrop.random_crop");
        StarTCustomTooltipsManager.writeCustomTooltipsToItem(genomeHolderFilled.getOrCreateTag(),
                "behaviour.start_core.genome_holder.copied_holder");

        GTRecipe harvestingRecipe = StarTRecipeTypes.GENOME_GATHERING
                .recipeBuilder("holder_harvesting")
                .inputItems(genomeHolderEmpty.copyWithCount(1), gCrop.copyWithCount(1))
                .outputItems(genomeHolderFilled)
                .duration(120)
                .EUt(GTValues.V[GTValues.MV])
                .buildRawRecipe();

        StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GENOME_GATHERING, "gcrops",
                harvestingRecipe);
    }
}
