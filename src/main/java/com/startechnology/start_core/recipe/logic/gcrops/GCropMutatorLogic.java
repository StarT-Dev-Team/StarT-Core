package com.startechnology.start_core.recipe.logic.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.api.gcrop.*;
import com.startechnology.start_core.data.gcrops.StarTTraitData;
import com.startechnology.start_core.item.gcrops.StarTGCropItems;
import com.startechnology.start_core.item.components.StarTGCropBehaviour;
import com.startechnology.start_core.recipe.StarTRecipeTypes;

import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;

import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.startechnology.start_core.item.gcrops.StarTGCropItems.GCROP_MALFORMED;
import static com.startechnology.start_core.utils.StarTMaterialUtils.getMaterial;
import static com.startechnology.start_core.utils.StarTItemUtils.getItem;

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

    public static boolean hasItemMatch(Item item, List<Item> itemList) {
        for (Item newItem : itemList) {
            if (item.equals(newItem)) return true;
        }
        return false;
    }

    public static boolean hasFluidMatch(Fluid fluid, List<Fluid> fluidList) {
        for (Fluid newFluid : fluidList) {
            if (fluid.isSame(newFluid)) return true;
        }
        return false;
    }

    private static int findTraitTier(List<ItemStack> mutationItems, List<FluidStack> mutationFluids, String type) {
        final List<Item> validMutationItemList = List.of(
                ChemicalHelper.get(dust, EnderPearl).getItem(),
                ChemicalHelper.get(dust, Thorium).getItem(),
                ChemicalHelper.get(dust, Caesium).getItem(),
                ChemicalHelper.get(dust, Tantalum).getItem(),
                ChemicalHelper.get(dust, Uranium235).getItem(),
                ChemicalHelper.get(dust, getMaterial("purified_naquadah")).getItem(),
                ChemicalHelper.get(dust, Americium).getItem());

        final List<Fluid> validMutationFluidList = List.of(
                Arsenic.getFluid(),
                Fluorine.getFluid(),
                Radon.getFluid(),
                IndiumGalliumPhosphide.getFluid(),
                Naquadria.getFluid(),
                GTMaterials.get("echo_r").getFluid());

        int maxTier = 0;
        List<Integer> tiers = new ArrayList<>();

        if (!type.equals("fluid")) {
            for (var item : mutationItems) {
                if (validMutationItemList.contains(item.getItem())) {
                    int tier = validMutationItemList.indexOf(item.getItem()) + 1;
                    if (!type.equals("full") && maxTier < tier) maxTier = tier;
                    else {
                        tiers.add(tier);
                    }
                }
            }
        }

        if (!type.equals("item")) {
            for (var fluid : mutationFluids) {
                if (validMutationFluidList.contains(fluid.getFluid())) {
                    int tier = validMutationFluidList.indexOf(fluid.getFluid()) + 2;
                    if (!type.equals("full") && maxTier < tier) maxTier = tier;
                    else {
                        if (tiers.contains(tier)) maxTier = tier;
                    }
                }
            }
        }

        if (tiers.contains(1)) maxTier = 1;

        return maxTier;
    }

    public static GTRecipe createGCropRecipe(List<ItemStack> itemSet, List<FluidStack> allFluids) {
        final List<Item> validMutationItemList = List.of(
                ChemicalHelper.get(dust, EnderPearl).getItem(),
                ChemicalHelper.get(dust, Thorium).getItem(),
                ChemicalHelper.get(dust, Caesium).getItem(),
                ChemicalHelper.get(dust, Tantalum).getItem(),
                ChemicalHelper.get(dust, Uranium235).getItem(),
                ChemicalHelper.get(dust, getMaterial("purified_naquadah")).getItem(),
                ChemicalHelper.get(dust, Americium).getItem());

        final List<Fluid> validMutationFluidList = List.of(
                Arsenic.getFluid(),
                Fluorine.getFluid(),
                Radon.getFluid(),
                IndiumGalliumPhosphide.getFluid(),
                Naquadria.getFluid(),
                GTMaterials.get("echo_r").getFluid(),
                getMaterial("start_core:mystical_air").getFluid());

        ItemStack foundGCrop = ItemStack.EMPTY;

        List<ItemStack> validMutationItems = new ArrayList<>();
        List<FluidStack> validMutationFluids = new ArrayList<>();

        for (ItemStack item : itemSet) {
            if (StarTGCropBehaviour.getGCropBehaviour(item) != null) {
                foundGCrop = item;
            } else if (hasItemMatch(item.getItem(), validMutationItemList)) {
                validMutationItems.add(item);
            }
        }

        for (FluidStack fluid : allFluids) {
            if (hasFluidMatch(fluid.getFluid(), validMutationFluidList)) {
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

        StarTGCropTrait newClimateGenome = null;
        List<StarTGCropTrait> climateTraits = new ArrayList<>(
                StarTGCropTraits.getTraitsByType(StarTTraitData.GenomeType.CLIMATE));

        int totalFrequency = 5000;
        for (var trait : climateTraits) {
            totalFrequency += trait.frequency();
        }

        Collections.shuffle(climateTraits);

        int hitFrequency = StarTCore.RNG.nextIntBetweenInclusive(1, totalFrequency);
        for (var trait : climateTraits) {
            int frequency = trait.frequency();
            if (hitFrequency < frequency) {
                newClimateGenome = trait;
                break;
            }
            hitFrequency -= frequency;
        }

        ItemStack newGCrop;
        if (!validMutationItems.isEmpty() && !validMutationFluids.isEmpty()) {
            int maxTier = findTraitTier(validMutationItems, validMutationFluids, "full");

            if (maxTier == 0) return null;

            List<StarTGCropTrait> mutatedTraits = StarTGCropTraits
                    .getTraitsBetweenTiersInclusive(maxTier - 1, maxTier);

            if (hasFluidMatch(getMaterial("start_core:mystical_air").getFluid(1), validMutationFluids)) {
                // full recipes
                List<StarTGCropGene> newResourceGenome = new ArrayList<>(
                        existingResourceGenome.stream().filter(gene -> gene.getTrait().tier() < maxTier - 1).toList());
                List<StarTGCropGene> newProductionGenome = new ArrayList<>(
                        existingProductionGenome.stream().filter(gene -> gene.getTrait().tier() < maxTier - 1)
                                .toList());
                List<StarTGCropGene> newAuxiliaryGenome = new ArrayList<>(
                        existingAuxiliaryGenome.stream().filter(gene -> gene.getTrait().tier() < maxTier - 1).toList());

                for (var trait : mutatedTraits) {
                    int alleleCount = trait.runTraitFrequencyRandomGene();
                    if (alleleCount >= 1) {
                        switch (trait.genomeType()) {
                            case RESOURCE -> newResourceGenome.add(new StarTGCropGene(trait, alleleCount));
                            case PRODUCTION -> newProductionGenome.add(new StarTGCropGene(trait, alleleCount));
                            case AUXILIARY -> newAuxiliaryGenome.add(new StarTGCropGene(trait, alleleCount));
                        }
                    }
                }

                newGCrop = StarTGCropTraits.getCropWithTraits(newResourceGenome, newProductionGenome,
                        newAuxiliaryGenome, new StarTGCropGene(newClimateGenome, 1));

                GTRecipeBuilder mutatorRecipe = StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                        .recipeBuilder(String.format("full_mutation_%s_to_%s", maxTier - 1, maxTier))
                        .inputItems(foundGCrop.copyWithCount(1))
                        .inputItems(new ItemStack(validMutationItemList.get(maxTier - 1)))
                        .inputFluids(getMaterial("start_core:mystical_air").getFluid(1000))
                        .outputItems(newGCrop.copyWithCount(1))
                        .duration(400)
                        .EUtV(StarTGCropItems.tierVoltages.get(maxTier));

                if (maxTier >= 2)
                    mutatorRecipe.inputFluids(new FluidStack(validMutationFluidList.get(maxTier - 2), 1000));

                return mutatorRecipe.buildRawRecipe();

            }
            // prod aux recipes
            List<StarTGCropGene> newProductionGenome = new ArrayList<>(
                    existingProductionGenome.stream().filter(gene -> gene.getTrait().tier() < maxTier - 1).toList());
            List<StarTGCropGene> newAuxiliaryGenome = new ArrayList<>(
                    existingAuxiliaryGenome.stream().filter(gene -> gene.getTrait().tier() < maxTier - 1).toList());

            List<StarTGCropTrait> productionTraits = StarTGCropTraits
                    .getTraitsByType(StarTTraitData.GenomeType.PRODUCTION, mutatedTraits);
            List<StarTGCropTrait> auxiliaryTraits = StarTGCropTraits
                    .getTraitsByType(StarTTraitData.GenomeType.AUXILIARY, mutatedTraits);

            for (var trait : productionTraits) {
                int alleleCount = trait.runTraitFrequencyRandomGene();
                if (alleleCount >= 1) {
                    newProductionGenome.add(new StarTGCropGene(trait, alleleCount));
                }
            }

            for (var trait : auxiliaryTraits) {
                int alleleCount = trait.runTraitFrequencyRandomGene();
                if (alleleCount >= 1) {
                    newAuxiliaryGenome.add(new StarTGCropGene(trait, alleleCount));
                }
            }

            newGCrop = StarTGCropTraits.getCropWithTraits(existingResourceGenome, newProductionGenome,
                    newAuxiliaryGenome, new StarTGCropGene(newClimateGenome, 1));

            GTRecipeBuilder mutatorRecipe = StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                    .recipeBuilder(String.format("prod_aux_mutation_%s_to_%s", maxTier - 1, maxTier))
                    .inputItems(foundGCrop.copyWithCount(1))
                    .inputItems(new ItemStack(validMutationItemList.get(maxTier - 1)))
                    .outputItems(newGCrop.copyWithCount(1))
                    .duration(400)
                    .EUtV(StarTGCropItems.tierVoltages.get(maxTier));

            if (maxTier >= 2) mutatorRecipe.inputFluids(new FluidStack(validMutationFluidList.get(maxTier - 2), 1000));

            return mutatorRecipe.buildRawRecipe();
        }
        if (!validMutationItems.isEmpty()) {
            // prod recipes
            int maxTier = findTraitTier(validMutationItems, validMutationFluids, "items");

            if (maxTier == 0) return null;

            List<StarTGCropTrait> mutatedTraits = StarTGCropTraits
                    .getTraitsBetweenTiersInclusive(maxTier - 2, maxTier);

            List<StarTGCropGene> newProductionGenome = new ArrayList<>(
                    existingProductionGenome.stream().filter(gene -> gene.getTrait().tier() < maxTier - 2).toList());

            List<StarTGCropTrait> productionTraits = StarTGCropTraits
                    .getTraitsByType(StarTTraitData.GenomeType.PRODUCTION, mutatedTraits);

            for (var trait : productionTraits) {
                int alleleCount = trait.runTraitFrequencyRandomGene();
                if (alleleCount >= 1) {
                    newProductionGenome.add(new StarTGCropGene(trait, alleleCount));
                }
            }

            newGCrop = StarTGCropTraits.getCropWithTraits(existingResourceGenome, newProductionGenome,
                    existingAuxiliaryGenome, new StarTGCropGene(newClimateGenome, 1));

            GTRecipeBuilder mutatorRecipe = StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                    .recipeBuilder(String.format("prod_mutation_%s_to_%s", maxTier - 2, maxTier))
                    .inputItems(foundGCrop.copyWithCount(1))
                    .inputItems(new ItemStack(validMutationItemList.get(maxTier - 1)))
                    .outputItems(newGCrop.copyWithCount(1))
                    .duration(400)
                    .EUtV(StarTGCropItems.tierVoltages.get(maxTier));

            return mutatorRecipe.buildRawRecipe();
        }
        // aux recipes
        int maxTier = findTraitTier(validMutationItems, validMutationFluids, "fluids");

        if (maxTier == 0) return null;

        List<StarTGCropTrait> mutatedTraits = StarTGCropTraits
                .getTraitsBetweenTiersInclusive(maxTier - 2, maxTier);

        List<StarTGCropGene> newAuxiliaryGenome = new ArrayList<>(
                existingAuxiliaryGenome.stream().filter(gene -> gene.getTrait().tier() < maxTier - 2).toList());

        List<StarTGCropTrait> auxiliaryTraits = StarTGCropTraits
                .getTraitsByType(StarTTraitData.GenomeType.AUXILIARY, mutatedTraits);

        for (var trait : auxiliaryTraits) {
            int alleleCount = trait.runTraitFrequencyRandomGene();
            if (alleleCount >= 1) {
                newAuxiliaryGenome.add(new StarTGCropGene(trait, alleleCount));
            }
        }

        newGCrop = StarTGCropTraits.getCropWithTraits(existingResourceGenome, existingProductionGenome,
                newAuxiliaryGenome, new StarTGCropGene(newClimateGenome, 1));

        GTRecipeBuilder mutatorRecipe = StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                .recipeBuilder(String.format("aux_mutation_%s_to_%s", maxTier - 2, maxTier))
                .inputItems(foundGCrop.copyWithCount(1))
                .inputFluids(new FluidStack(validMutationFluidList.get(maxTier - 2), 1000))
                .outputItems(newGCrop.copyWithCount(1))
                .duration(400)
                .EUtV(StarTGCropItems.tierVoltages.get(maxTier));

        return mutatorRecipe.buildRawRecipe();
    }

    @Override
    public void buildRepresentativeRecipes() {
        final List<Item> validMutationItemList = List.of(
                ChemicalHelper.get(dust, EnderPearl).getItem(),
                ChemicalHelper.get(dust, Thorium).getItem(),
                ChemicalHelper.get(dust, Caesium).getItem(),
                ChemicalHelper.get(dust, Tantalum).getItem(),
                ChemicalHelper.get(dust, Uranium235).getItem(),
                ChemicalHelper.get(dust, getMaterial("purified_naquadah")).getItem(),
                ChemicalHelper.get(dust, Americium).getItem());

        final List<Fluid> validMutationFluidList = List.of(
                Arsenic.getFluid(),
                Fluorine.getFluid(),
                Radon.getFluid(),
                IndiumGalliumPhosphide.getFluid(),
                Naquadria.getFluid(),
                GTMaterials.get("echo_r").getFluid(),
                getMaterial("start_core:mystical_air").getFluid());

        ItemStack gCropRandomSeed = new ItemStack(GCROP_MALFORMED.asItem());
        gCropRandomSeed.setHoverName(Component.translatable(
                "behaviour.start_core.gcrop.random_crop_name"));

        for (int i = 1; i <= 7; i++) {
            ItemStack fullMutatedSeed = new ItemStack(GCROP_MALFORMED.asItem());
            fullMutatedSeed.setHoverName(Component.translatable(
                    "behaviour.start_core.gcrop.random_crop_name"));

            StarTCustomTooltipsManager.writeCustomTooltipsToItem(fullMutatedSeed.getOrCreateTag(),
                    Component.translatable("behaviour.start_core.gcrop.mutator.full", i - 1, i).getString());

            GTRecipeBuilder fullMutationRecipe = StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                    .recipeBuilder(String.format("full_mutation_%s_to_%s", i - 1, i))
                    .inputItems(gCropRandomSeed)
                    .inputItems(new ItemStack(validMutationItemList.get(i - 1)))
                    .inputFluids(getMaterial("start_core:mystical_air").getFluid(1000))
                    .outputItems(fullMutatedSeed)
                    .duration(400)
                    .EUtV(StarTGCropItems.tierVoltages.get(i));

            if (i >= 2) fullMutationRecipe.inputFluids(new FluidStack(validMutationFluidList.get(i - 2), 1000));

            StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GCROP_MUTATOR_RECIPES, "gcrops",
                    fullMutationRecipe.buildRawRecipe());

            ItemStack prodAuxMutatedSeed = new ItemStack(GCROP_MALFORMED.asItem());
            fullMutatedSeed.setHoverName(Component.translatable(
                    "behaviour.start_core.gcrop.random_crop_name"));

            StarTCustomTooltipsManager.writeCustomTooltipsToItem(prodAuxMutatedSeed.getOrCreateTag(),
                    Component.translatable("behaviour.start_core.gcrop.mutator.prod_aux", i - 1, i).getString());

            GTRecipeBuilder prodAuxMutationRecipe = StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                    .recipeBuilder(String.format("prod_aux_mutation_%s_to_%s", i - 1, i))
                    .inputItems(gCropRandomSeed)
                    .inputItems(new ItemStack(validMutationItemList.get(i - 1)))
                    .outputItems(prodAuxMutatedSeed)
                    .duration(400)
                    .EUtV(StarTGCropItems.tierVoltages.get(i));

            if (i >= 2) prodAuxMutationRecipe.inputFluids(new FluidStack(validMutationFluidList.get(i - 2), 1000));

            StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GCROP_MUTATOR_RECIPES, "gcrops",
                    prodAuxMutationRecipe.buildRawRecipe());

            if (i > 1) {
                ItemStack prodMutatedSeed = new ItemStack(GCROP_MALFORMED.asItem());
                fullMutatedSeed.setHoverName(Component.translatable(
                        "behaviour.start_core.gcrop.random_crop_name"));

                StarTCustomTooltipsManager.writeCustomTooltipsToItem(prodMutatedSeed.getOrCreateTag(),
                        Component.translatable("behaviour.start_core.gcrop.mutator.prod", i - 2, i).getString());

                ItemStack auxMutatedSeed = new ItemStack(GCROP_MALFORMED.asItem());
                fullMutatedSeed.setHoverName(Component.translatable(
                        "behaviour.start_core.gcrop.random_crop_name"));

                StarTCustomTooltipsManager.writeCustomTooltipsToItem(auxMutatedSeed.getOrCreateTag(),
                        Component.translatable("behaviour.start_core.gcrop.mutator.aux", i - 2, i).getString());

                GTRecipe prodMutationRecipe = StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                        .recipeBuilder(String.format("prod_mutation_%s_to_%s", i - 2, i))
                        .inputItems(gCropRandomSeed)
                        .inputItems(new ItemStack(validMutationItemList.get(i - 1)))
                        .outputItems(prodMutatedSeed)
                        .duration(400)
                        .EUtV(StarTGCropItems.tierVoltages.get(i))
                        .buildRawRecipe();

                GTRecipe auxMutationRecipe = StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                        .recipeBuilder(String.format("aux_mutation_%s_to_%s", i - 2, i))
                        .inputItems(gCropRandomSeed)
                        .inputFluids(new FluidStack(validMutationFluidList.get(i - 2), 1000))
                        .outputItems(auxMutatedSeed)
                        .duration(400)
                        .EUtV(StarTGCropItems.tierVoltages.get(i))
                        .buildRawRecipe();

                StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GCROP_MUTATOR_RECIPES, "gcrops",
                        prodMutationRecipe);

                StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.GCROP_MUTATOR_RECIPES, "gcrops",
                        auxMutationRecipe);
            }
        }
    }
}
