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

import static com.startechnology.start_core.item.gcrops.StarTGCropItems.*;

public class GenomeHarvestingLogic implements ICustomRecipeLogic {

    /**
     * Scans item handlers for a GCrop and an empty genome holder to create a genome extraction recipe.
     *
     * @param holder the recipe capability holder representing the machine
     * @return the constructed {@link GTRecipe}, or {@code null} if matching items are not present
     */
    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var itemHandlers = StarTCustomLogicUtils.getItemHandlers(holder);
        if (itemHandlers.isEmpty()) return null;

        List<ItemStack> allItems = StarTCustomLogicUtils.getAllItems(itemHandlers);

        return createGenomeHarvestRecipe(allItems);
    }

    /**
     * Extracts the complete genome from a GCrop item into an empty genome holder item.
     *
     * @param itemSet the list of available input items
     * @return the constructed genome harvesting {@link GTRecipe}, or {@code null} if inputs are invalid
     */
    private GTRecipe createGenomeHarvestRecipe(List<ItemStack> itemSet) {
        ItemStack foundHolder = null;
        ItemStack foundGCrop = null;
        StarTGCropBehaviour cropBehaviour = null;
        StarTGCropGenome gCropGenome = null;

        for (ItemStack item : itemSet) {
            if (item.isEmpty()) continue;

            if (foundGCrop == null) {
                StarTGCropBehaviour behaviour = StarTGCropBehaviour.getGCropBehaviour(item);
                if (behaviour != null) {
                    StarTGCropGenome genome = StarTGCropManager.gcropGenomeFromTag(item);
                    if (genome != null) {
                        foundGCrop = item;
                        cropBehaviour = behaviour;
                        gCropGenome = genome;
                        if (foundHolder != null) break;
                        continue;
                    }
                }
            }

            if (foundHolder == null && item.is(EMPTY_GENOME_HOLDER.get())) {
                foundHolder = item;
                if (foundGCrop != null) break;
            }
        }

        if (foundHolder == null || foundGCrop == null || cropBehaviour == null || gCropGenome == null) {
            return null;
        }

        int cropTier = cropBehaviour.getCropTier();
        ItemStack newHolder = FILLED_GENOME_HOLDER.asStack();
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
        ItemStack genomeHolderEmpty = EMPTY_GENOME_HOLDER.asStack();
        ItemStack genomeHolderFilled = FILLED_GENOME_HOLDER.asStack();
        ItemStack gCrop = GCROP_MALFORMED.asStack();

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
