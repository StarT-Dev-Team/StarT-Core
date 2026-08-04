package com.startechnology.start_core.recipe.logic.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.api.gcrop.*;
import com.startechnology.start_core.item.components.StarTGenomeHolderBehaviour;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.startechnology.start_core.item.gcrops.StarTGCropItems.*;

public class GenomeMixingLogic implements ICustomRecipeLogic {

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var itemHandlers = StarTCustomLogicUtils.getItemHandlers(holder);
        if (itemHandlers.isEmpty()) return null;

        List<ItemStack> allItems = StarTCustomLogicUtils.getAllItems(itemHandlers);

        return createGenomeDuplicationRecipe(allItems);
    }

    private List<StarTGCropGene> geneMapToGenome(HashMap<StarTGCropTrait, Integer> geneMap) {
        List<StarTGCropGene> newGenome = new ArrayList<>();

        for (StarTGCropTrait trait : geneMap.keySet()) {
            int traitCount = geneMap.get(trait);
            newGenome.add(new StarTGCropGene(trait, traitCount));
        }

        return newGenome;
    }

    private GTRecipe createGenomeDuplicationRecipe(List<ItemStack> itemSet) {
        ItemStack foundFirstholder = null;
        ItemStack foundSecondHolder = null;

        for (ItemStack item : itemSet) {
            if (StarTGenomeHolderBehaviour.getGenomeHolderBehaviour(item) != null) {
                if (StarTGCropManager.gcropGenomeFromTag(item) != null) {
                    if (foundFirstholder == null) foundFirstholder = item;
                    else if (foundSecondHolder == null) foundSecondHolder = item;
                    else break;
                }
            }
        }

        if (foundFirstholder == null || foundSecondHolder == null) return null;

        StarTGCropGenome firstGenome = StarTGCropManager.gcropGenomeFromTag(foundFirstholder);
        StarTGCropGenome secondGenome = StarTGCropManager.gcropGenomeFromTag(foundSecondHolder);
        assert firstGenome != null;
        assert secondGenome != null;

        int highestTier = 0;
        StarTGCropGene climateGene = null;

        HashMap<StarTGCropTrait, Integer> resourceMap = new HashMap<>();
        HashMap<StarTGCropTrait, Integer> productionMap = new HashMap<>();
        HashMap<StarTGCropTrait, Integer> auxiliaryMap = new HashMap<>();

        for (StarTGCropGenome genome : List.of(firstGenome, secondGenome)) {
            List<StarTGCropGene> resourceGenome = genome.getResourceGenome();
            List<StarTGCropGene> productionGenome = genome.getProductionGenome();
            List<StarTGCropGene> auxiliaryGenome = genome.getAuxiliaryGenome();

            HashMap<List<StarTGCropGene>, HashMap<StarTGCropTrait, Integer>> genomeHashMapMap = new HashMap<>();

            genomeHashMapMap.put(resourceGenome, resourceMap);
            genomeHashMapMap.put(productionGenome, productionMap);
            genomeHashMapMap.put(auxiliaryGenome, auxiliaryMap);

            for (List<StarTGCropGene> genomeList : genomeHashMapMap.keySet()) {
                HashMap<StarTGCropTrait, Integer> storageMap = genomeHashMapMap.get(genomeList);

                for (StarTGCropGene gene : genomeList) {
                    var trait = gene.getTrait();
                    boolean traitExists = storageMap.containsKey(trait);
                    int alleleCount = gene.getDominantAlleles();

                    if (traitExists) {
                        int currentCount = storageMap.get(trait);
                        alleleCount = Math.min(currentCount + alleleCount, trait.alleleCount());
                    }

                    storageMap.put(trait, alleleCount);
                }
            }

            if (climateGene == null) {
                climateGene = genome.getClimateGene();
            } else {
                if (StarTCore.RNG.nextIntBetweenInclusive(0, 1) == 1) climateGene = genome.getClimateGene();
            }
        }

        List<StarTGCropGene> newResourceGenome = geneMapToGenome(resourceMap);
        List<StarTGCropGene> newProductionGenome = geneMapToGenome(productionMap);
        List<StarTGCropGene> newAuxiliaryGenome = geneMapToGenome(auxiliaryMap);

        ItemStack newHolder = new ItemStack(FILLED_GENOME_HOLDER);
        ItemStack emptyHolder = new ItemStack(EMPTY_GENOME_HOLDER);

        StarTGCropGenome newGenome = new StarTGCropGenome(newResourceGenome, newProductionGenome, newAuxiliaryGenome,
                climateGene);
        StarTGCropManager.writeGCRopGenomeToItem(newHolder.getOrCreateTag(), newGenome);

        return StarTRecipeTypes.GENOME_MIXING
                .recipeBuilder("holder_mixing")
                .inputItems(foundFirstholder.copyWithCount(1), foundSecondHolder.copyWithCount(1))
                .outputItems(newHolder, emptyHolder)
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
