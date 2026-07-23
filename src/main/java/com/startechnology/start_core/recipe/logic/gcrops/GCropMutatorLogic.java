package com.startechnology.start_core.recipe.logic.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.api.gcrop.*;
import com.startechnology.start_core.data.gcrops.StarTGCropTraits;
import com.startechnology.start_core.item.components.StarTGCropBehaviour;
import com.startechnology.start_core.recipe.StarTRecipeTypes;

import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;

import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.startechnology.start_core.item.StarTGCropItems.GCROP_MALFORMED;

public class GCropMutatorLogic implements ICustomRecipeLogic {

    public GCropMutatorLogic() {}

    @Override
    public GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var itemHandlers = StarTCustomLogicUtils.getItemHandlersMap(holder);

        var fluidHandlers = StarTCustomLogicUtils.getFluidHandlersMap(holder);

        if (itemHandlers.isEmpty() || fluidHandlers.isEmpty()) return null;

        List<List<ItemStack>> allItems = StarTCustomLogicUtils.getAllItems(itemHandlers);
        List<FluidStack> allFluids = StarTCustomLogicUtils.getAllFluids(fluidHandlers);

        for (List<ItemStack> itemSet : allItems) {
            GTRecipe recipe = createGCropRecipe(itemSet, allFluids);
            if (recipe != null) return recipe;
        }

        return null;
    }

    public static boolean hasItemMatch(ItemStack item, List<ItemStack> itemList) {
        for (ItemStack newItem : itemList) {
            if (ItemStack.isSameItem(item, newItem)) return true;
        }
        return false;
    }

    public static boolean hasFluidMatch(FluidStack fluid, List<FluidStack> fluidList) {
        for (FluidStack newFluid : fluidList) {
            if (fluid.isFluidEqual(newFluid)) return true;
        }
        return false;
    }

    public static GTRecipe createGCropRecipe(List<ItemStack> itemSet, List<FluidStack> allFluids) {
        final List<ItemStack> validMutationItemList = List.of(
                ChemicalHelper.get(dust, Thorium),
                ChemicalHelper.get(dust, Uranium238),
                ChemicalHelper.get(dust, EnderPearl));

        final List<FluidStack> validMutationFluidList = List.of(
                GTMaterials.Radon.getFluid(1),
                GTMaterials.Naquadria.getFluid(1),
                GTMaterials.Air.getFluid(1));

        ItemStack foundGCrop = ItemStack.EMPTY;

        List<ItemStack> validMutationItems = new ArrayList<>();
        List<FluidStack> validMutationFluids = new ArrayList<>();

        for (ItemStack item : itemSet) {
            if (StarTGCropBehaviour.getGCropBehaviour(item) != null) {
                foundGCrop = item;
            } else if (hasItemMatch(item, validMutationItemList)) {
                validMutationItems.add(item);
            }
        }

        for (FluidStack fluid : allFluids) {
            if (hasFluidMatch(fluid, validMutationFluidList)) {
                validMutationFluids.add(fluid);
            }
        }

        if (foundGCrop.isEmpty() || (validMutationItems.isEmpty() && validMutationFluids.isEmpty())) return null;

        StarTGCropGenome existingStats = StarTGCropManager.gcropGenomeFromTag(foundGCrop);
        if (existingStats == null) {
            List<StarTGCropGene> emptyTraits = new ArrayList<>();

            ItemStack newGCrop = StarTGCropTraits.getCropWithTraits(emptyTraits, emptyTraits, emptyTraits);

            return StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                    .recipeBuilder("create_genome")
                    .inputItems(foundGCrop.copyWithCount(1))
                    .outputItems(newGCrop.copyWithCount(1))
                    .duration(400)
                    .EUtV(GTValues.MV)
                    .buildRawRecipe();
        }

        List<StarTGCropGene> existingResourceGenome = existingStats.getResourceGenome();
        List<StarTGCropGene> existingProductionGenome = existingStats.getProductionGenome();
        List<StarTGCropGene> existingAuxiliaryGenome = existingStats.getAuxiliaryGenome();

        ItemStack newGCrop = foundGCrop.copyWithCount(1);

        if (hasFluidMatch(GTMaterials.Radon.getFluid(1), validMutationFluids)) {
            // Mutate tier 0-3 aux genome
            List<StarTGCropTraits.StarTGCropTrait> lowTierTraits = StarTGCropTraits.getTraitsBelowTierInclusive(3);

            List<StarTGCropTraits.StarTGCropTrait> lowTierAuxTraits = StarTGCropTraits
                    .getTraitsByType(StarTGCropTraits.GenomeType.AUXILIARY, lowTierTraits);

            List<StarTGCropGene> newAuxiliaryGenome = new ArrayList<>();

            for (var trait : lowTierAuxTraits) {
                int alleleCount = trait.runTraitFrequencyRandomGene(2);
                if (alleleCount >= 1) newAuxiliaryGenome.add(new StarTGCropGene(trait, alleleCount));
            }

            newGCrop = StarTGCropTraits.getCropWithTraits(existingResourceGenome, existingProductionGenome,
                    newAuxiliaryGenome);

            return StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                    .recipeBuilder("aux_mutation_0_to_3")
                    .inputItems(foundGCrop.copyWithCount(1))
                    .inputFluids(GTMaterials.Radon.getFluid(1000))
                    .outputItems(newGCrop.copyWithCount(1))
                    .duration(400)
                    .EUtV(GTValues.MV)
                    .buildRawRecipe();
        }

        if (hasItemMatch(ChemicalHelper.get(dust, EnderPearl), validMutationItems) &&
                hasFluidMatch(GTMaterials.Air.getFluid(1), validMutationFluids)) {
            // Mutate tier 0-1 full genome
            List<StarTGCropTraits.StarTGCropTrait> lowTierTraits = StarTGCropTraits.getTraitsBelowTierInclusive(1);

            List<StarTGCropGene> newResourceGenome = new ArrayList<>();
            List<StarTGCropGene> newProductionGenome = new ArrayList<>();
            List<StarTGCropGene> newAuxiliaryGenome = new ArrayList<>();

            for (var trait : lowTierTraits) {
                int alleleCount = trait.runTraitFrequencyRandomGene(2);
                if (alleleCount >= 1) {
                    switch (trait.genomeType()) {
                        case RESOURCE -> {
                            newResourceGenome.add(new StarTGCropGene(trait, alleleCount));
                        }
                        case PRODUCTION -> {
                            newProductionGenome.add(new StarTGCropGene(trait, alleleCount));
                        }
                        case AUXILIARY -> {
                            newAuxiliaryGenome.add(new StarTGCropGene(trait, alleleCount));
                        }
                    }
                }
            }
            newGCrop = StarTGCropTraits.getCropWithTraits(newResourceGenome, newProductionGenome,
                    newAuxiliaryGenome);

            return StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                    .recipeBuilder("full_mutation_0_to_1")
                    .inputItems(foundGCrop.copyWithCount(1))
                    .inputItems(ChemicalHelper.get(dust, EnderPearl).copyWithCount(4))
                    .inputFluids(GTMaterials.Air.getFluid(1000))
                    .outputItems(newGCrop.copyWithCount(1))
                    .duration(400)
                    .EUtV(GTValues.MV)
                    .buildRawRecipe();
        }

        return null;
    }

    @Override
    public void buildRepresentativeRecipes() {
        ItemStack gCropRandomSeed = new ItemStack(GCROP_MALFORMED.asItem());
        StarTCustomTooltipsManager.writeCustomTooltipsToItem(gCropRandomSeed.getOrCreateTag(),
                "behaviour.start_core.gcrop.random_crop");

        gCropRandomSeed.setHoverName(Component.translatable(
                "behaviour.start_core.gcrop.random_crop_name"));

        ItemStack gCropInput = new ItemStack(GCROP_MALFORMED.get());
        StarTCustomTooltipsManager.writeCustomTooltipsToItem(gCropInput.getOrCreateTag(),
                "behaviour.start_core.bacteria.input");

        GTRecipe AuxMutation0_3Recipe = StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                .recipeBuilder("aux_mutation_0_to_3")
                .inputItems(gCropInput.copyWithCount(1))
                .inputFluids(GTMaterials.Radon.getFluid(1000))
                .outputItems(gCropRandomSeed)
                .duration(400)
                .EUtV(GTValues.MV)
                .buildRawRecipe();

        GTRecipe fullMutation0_1Recipe = StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                .recipeBuilder("full_mutation_0_to_1")
                .inputItems(gCropInput.copyWithCount(1))
                .inputFluids(GTMaterials.Air.getFluid(1000))
                .inputItems(ChemicalHelper.get(dust, EnderPearl).copyWithCount(4))
                .outputItems(gCropRandomSeed)
                .duration(400)
                .EUtV(GTValues.MV)
                .buildRawRecipe();

        StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GCROP_MUTATOR_RECIPES, AuxMutation0_3Recipe);
        StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GCROP_MUTATOR_RECIPES, fullMutation0_1Recipe);
    }
}
