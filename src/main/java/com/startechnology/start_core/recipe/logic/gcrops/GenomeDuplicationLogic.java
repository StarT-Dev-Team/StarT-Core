package com.startechnology.start_core.recipe.logic.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.api.gcrop.StarTGCropGene;
import com.startechnology.start_core.api.gcrop.StarTGCropGenome;
import com.startechnology.start_core.api.gcrop.StarTGCropManager;
import com.startechnology.start_core.data.gcrops.StarTGCropTraits;
import com.startechnology.start_core.item.components.StarTGenomeHolderBehaviour;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.startechnology.start_core.item.gcrops.StarTGCropItems.EMPTY_GENOME_HOLDER;
import static com.startechnology.start_core.item.gcrops.StarTGCropItems.FILLED_GENOME_HOLDER;

public class GenomeDuplicationLogic implements ICustomRecipeLogic {

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var itemHandlers = StarTCustomLogicUtils.getItemHandlers(holder);
        if (itemHandlers.isEmpty()) return null;

        List<ItemStack> allItems = StarTCustomLogicUtils.getAllItems(itemHandlers);

        return createGenomeDuplicationRecipe(allItems);
    }

    private GTRecipe createGenomeDuplicationRecipe(List<ItemStack> itemSet) {
        ItemStack foundFilledHolder = null;
        ItemStack foundEmptyHolder = null;

        for (ItemStack item : itemSet) {
            if (StarTGenomeHolderBehaviour.getGenomeHolderBehaviour(item) != null) {
                if (StarTGCropManager.gcropGenomeFromTag(item) != null) {
                    if (foundFilledHolder == null) foundFilledHolder = item;
                }
            } else if (item.getItem().equals(EMPTY_GENOME_HOLDER.asItem())) {
                if (foundEmptyHolder == null) foundEmptyHolder = item;
            }
        }

        if (foundFilledHolder == null || foundEmptyHolder == null) return null;

        StarTGenomeHolderBehaviour holderBehaviour = StarTGenomeHolderBehaviour
                .getGenomeHolderBehaviour(foundFilledHolder);
        if (holderBehaviour == null) return null;

        StarTGCropGenome gCropGenome = StarTGCropManager.gcropGenomeFromTag(foundFilledHolder);
        if (gCropGenome == null) return null;

        int highestTier = 0;

        List<StarTGCropGene> resourceGenome = gCropGenome.getResourceGenome();
        List<StarTGCropGene> productionGenome = gCropGenome.getProductionGenome();
        List<StarTGCropGene> auxiliaryGenome = gCropGenome.getAuxiliaryGenome();
        StarTGCropGene climateGene = gCropGenome.getClimateGene();

        List<StarTGCropGene> fullGenome = new ArrayList<>(Stream
                .concat(resourceGenome.stream(), Stream.concat(productionGenome.stream(), auxiliaryGenome.stream()))
                .toList());
        fullGenome.add(climateGene);

        for (StarTGCropGene gene : fullGenome) {
            StarTGCropTraits.StarTGCropTrait trait = gene.getTrait();
            int tier = trait.tier();
            if (tier > highestTier) highestTier = tier;
        }

        return StarTRecipeTypes.GENOME_GATHERING
                .recipeBuilder("holder_duplication")
                .inputItems(foundFilledHolder.copyWithCount(1), foundEmptyHolder.copyWithCount(3))
                .inputFluids(GTMaterials.Dimethylamine.getFluid(1000))
                .outputItems(foundFilledHolder.copyWithCount(4))
                .duration(120)
                .EUtVA(GTValues.MV + highestTier)
                .buildRawRecipe();
    }

    @Override
    public void buildRepresentativeRecipes() {
        ItemStack genomeHolder = new ItemStack(FILLED_GENOME_HOLDER.asItem());
        ItemStack newGenomeHolder = new ItemStack(FILLED_GENOME_HOLDER.asItem(), 4);
        ItemStack emptyHolder = new ItemStack(EMPTY_GENOME_HOLDER.asItem(), 3);

        StarTCustomTooltipsManager.writeCustomTooltipsToItem(genomeHolder.getOrCreateTag(),
                "behaviour.start_core.genome_holder.random_holder");
        StarTCustomTooltipsManager.writeCustomTooltipsToItem(newGenomeHolder.getOrCreateTag(),
                "behaviour.start_core.genome_holder.copied_holder");

        GTRecipe duplicationRecipe = StarTRecipeTypes.GENOME_GATHERING
                .recipeBuilder("holder_duplication")
                .inputItems(genomeHolder, emptyHolder)
                .outputItems(newGenomeHolder)
                .duration(120)
                .EUt(GTValues.V[GTValues.MV])
                .buildRawRecipe();

        StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GENOME_GATHERING, "gcrops",
                duplicationRecipe);
    }
}
