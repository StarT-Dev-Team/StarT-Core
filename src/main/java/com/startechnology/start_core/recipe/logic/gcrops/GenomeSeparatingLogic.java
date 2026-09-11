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

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var itemHandlers = StarTCustomLogicUtils.getItemHandlers(holder);
        if (itemHandlers.isEmpty()) return null;

        List<ItemStack> allItems = StarTCustomLogicUtils.getAllItems(itemHandlers);

        return createGenomeSeparationRecipe(allItems);
    }

    private GTRecipe createGenomeSeparationRecipe(List<ItemStack> itemSet) {
        ItemStack foundFilledHolder = null;
        ItemStack foundEmptyHolder = null;

        for (ItemStack item : itemSet) {
            if (StarTGenomeHolderBehaviour.getGenomeHolderBehaviour(item) != null) {
                if (StarTGCropManager.gcropGenomeFromTag(item) != null) {
                    if (foundFilledHolder == null) foundFilledHolder = item;
                }
            } else {
                if (item.getItem().equals(EMPTY_GENOME_HOLDER.asItem())) {
                    if (foundEmptyHolder == null) foundEmptyHolder = item;
                }
            }
        }

        if (foundFilledHolder == null || foundEmptyHolder == null) return null;

        StarTGCropGenome gCropGenome = StarTGCropManager.gcropGenomeFromTag(foundFilledHolder);
        assert gCropGenome != null;

        int highestTier = 0;

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

        for (StarTGCropGene gene : resourceGenome) {
            if (StarTCore.RNG.nextIntBetweenInclusive(0, 1) == 1) newResourceGenome1.add(gene);
            else newResourceGenome2.add(gene);
            int tier = gene.getTrait().tier();
            if (tier > highestTier) highestTier = tier;
        }

        for (StarTGCropGene gene : productionGenome) {
            if (StarTCore.RNG.nextIntBetweenInclusive(0, 1) == 1) newProductionGenome1.add(gene);
            else newProductionGenome2.add(gene);
            int tier = gene.getTrait().tier();
            if (tier > highestTier) highestTier = tier;
        }

        for (StarTGCropGene gene : auxiliaryGenome) {
            if (StarTCore.RNG.nextIntBetweenInclusive(0, 1) == 1) newAuxiliaryGenome1.add(gene);
            else newAuxiliaryGenome2.add(gene);
            int tier = gene.getTrait().tier();
            if (tier > highestTier) highestTier = tier;
        }

        StarTGCropGenome newGenome1;
        StarTGCropGenome newGenome2;

        if (climateGene == null) {
            newGenome1 = new StarTGCropGenome(newResourceGenome1, newProductionGenome1, newAuxiliaryGenome1);
            newGenome2 = new StarTGCropGenome(newResourceGenome2, newProductionGenome2, newAuxiliaryGenome2);
        } else {
            boolean firstHasClimate = StarTCore.RNG.nextIntBetweenInclusive(0, 1) == 1;
            newGenome1 = new StarTGCropGenome(newResourceGenome1, newProductionGenome1, newAuxiliaryGenome1,
                    firstHasClimate ? climateGene : null);
            newGenome2 = new StarTGCropGenome(newResourceGenome2, newProductionGenome2, newAuxiliaryGenome2,
                    firstHasClimate ? null : climateGene);
        }

        ItemStack newHolder1 = new ItemStack(FILLED_GENOME_HOLDER);
        ItemStack newHolder2 = new ItemStack(FILLED_GENOME_HOLDER);

        if (newGenome1.isEmpty()) {
            newHolder1 = new ItemStack(EMPTY_GENOME_HOLDER);
        } else StarTGCropManager.writeGCRopGenomeToItem(newHolder1.getOrCreateTag(), newGenome1);

        if (newGenome2.isEmpty()) {
            newHolder2 = new ItemStack(EMPTY_GENOME_HOLDER);
        } else StarTGCropManager.writeGCRopGenomeToItem(newHolder2.getOrCreateTag(), newGenome2);

        return StarTRecipeTypes.GENOME_SEPARATING
                .recipeBuilder("holder_separation")
                .inputItems(foundFilledHolder.copyWithCount(1), foundEmptyHolder.copyWithCount(1))
                .outputItems(newHolder1, newHolder2)
                .duration(120)
                .EUtVA(GTValues.MV + highestTier)
                .buildRawRecipe();
    }

    @Override
    public void buildRepresentativeRecipes() {
        ItemStack randomHolder = new ItemStack(FILLED_GENOME_HOLDER.asItem());
        ItemStack newHolder = new ItemStack(FILLED_GENOME_HOLDER.asItem());
        ItemStack emptyHolder = new ItemStack(EMPTY_GENOME_HOLDER.asItem());

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
