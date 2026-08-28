package com.startechnology.start_core.recipe.logic.gcrops;

import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.api.gcrop.*;
import com.startechnology.start_core.item.components.StarTGCropBehaviour;
import com.startechnology.start_core.item.gcrops.StarTGCropItems;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

import static com.startechnology.start_core.item.gcrops.StarTGCropItems.*;

public class GCropHarvesterLogic implements ICustomRecipeLogic {

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var itemHandlers = StarTCustomLogicUtils.getItemHandlersMap(holder);
        if (itemHandlers.isEmpty()) return null;

        List<List<ItemStack>> allItems = StarTCustomLogicUtils.getAllItems(itemHandlers);

        for (List<ItemStack> itemSet : allItems) {
            GTRecipe recipe = createHarvesterRecipe(itemSet, holder);
            if (recipe != null) return recipe;
        }

        return null;
    }

    private GTRecipe createHarvesterRecipe(List<ItemStack> itemSet, IRecipeCapabilityHolder holder) {
        final HashMap<Integer, Fluid> tieredGrowthFluids = new HashMap<>() {

            {
                put(0, GTMaterials.Water.getFluid());
                put(2, GTMaterials.Biomass.getFluid());
                put(4, GTMaterials.get("npk_solution").getFluid());
                put(5, GTMaterials.get("nutrient_rich_fertilizer_solution").getFluid());
                put(6, GTMaterials.get("biostimulating_mixture").getFluid());
            }
        };

        final HashMap<Integer, Item> tieredGrowthItems = new HashMap<>() {

            {
                put(1, Items.BONE_MEAL);
                put(3, GTItems.FERTILIZER.asItem());
                put(5, ChemicalHelper.get(TagPrefix.dust, GTMaterials.Phosphate).getItem());
                put(7, ChemicalHelper.get(TagPrefix.dust, GTMaterials.Strontium).getItem());
            }
        };

        for (ItemStack potentialCrop : itemSet) {
            StarTGCropBehaviour cropBehaviour = StarTGCropBehaviour.getGCropBehaviour(potentialCrop);
            if (cropBehaviour == null) continue;

            StarTGCropGenome gCropGenome = StarTGCropManager.gcropGenomeFromTag(potentialCrop);
            if (gCropGenome == null) continue;

            ItemEntry<ComponentItem> fruit = GCROP_FRUITMAP.get(cropBehaviour.getCropMaterial());
            if (fruit == null) continue;

            int cropTier = cropBehaviour.getCropTier();

            int duration = (cropTier == 0) ? 160 : 160 * cropTier;
            if (gCropGenome.hasTrait("quickened")) duration = (int) Math.round(duration * 0.9);
            if (gCropGenome.hasTrait("speedy")) duration = (int) Math.round(duration * 0.9);
            if (gCropGenome.hasTrait("fast")) duration = (int) Math.round(duration * 0.9);

            if (gCropGenome.hasTrait("slow")) duration = (int) Math.round(duration * 1.2);
            if (gCropGenome.hasTrait("stunted")) duration = (int) Math.round(duration * 1.2);

            if (gCropGenome.hasTrait("early")) {
                duration = (int) Math.round(duration * 0.7);
                ItemEntry<ComponentItem> flower = GCROP_FLOWERMAP.get(cropBehaviour.getCropMaterial());
                if (flower != null) fruit = flower;
            }

            int EUtV = StarTGCropItems.tierVoltages.get(cropTier);
            if (gCropGenome.hasTrait("empowered")) EUtV -= 1;

            Item fertilizerItem = null;
            int foundTierItem = 0;
            for (int j = cropTier; j >= 0; j--) {
                if (tieredGrowthItems.containsKey(j)) {
                    fertilizerItem = tieredGrowthItems.get(j);
                    foundTierItem = j;
                    break;
                }
            }
            int fertilizerAmount = 1 << (2 * (cropTier - foundTierItem));

            Fluid growthFluid = null;
            int foundTierFluid = 0;
            for (int j = cropTier; j >= 0; j--) {
                if (tieredGrowthFluids.containsKey(j)) {
                    growthFluid = tieredGrowthFluids.get(j);
                    foundTierFluid = j;
                    break;
                }
            }
            int fluidAmount = 100 << (2 * (cropTier - foundTierFluid));
            if (gCropGenome.hasTrait("thirsty")) fluidAmount = (int) Math.round(fluidAmount * 1.2);

            if (gCropGenome.hasTrait("gluttonous")) {
                fertilizerAmount = (int) Math.round(fertilizerAmount * 1.2);
                fluidAmount = (int) Math.round(fluidAmount * 1.2);
            }

            int minFruitAmount = 1;
            int maxFruitAmount = 4;
            if (gCropGenome.hasTrait("enormous")) {
                if (StarTCore.RNG.nextIntBetweenInclusive(1, 100) < 60) maxFruitAmount += 2;
            }

            if (gCropGenome.hasTrait("branching")) {
                for (int j = 0; j < 3; j++) if (StarTCore.RNG.nextIntBetweenInclusive(1, 100) < 60) maxFruitAmount += 2;
                for (int j = 0; j < 2; j++) if (StarTCore.RNG.nextIntBetweenInclusive(1, 100) < 70) minFruitAmount += 1;
            }

            if (gCropGenome.hasTrait("shriveled")) {
                maxFruitAmount -= 3;
                minFruitAmount -= 2;
            }

            if (gCropGenome.hasTrait("proliferating")) {
                fertilizerAmount = fertilizerAmount * 2;
                fluidAmount = fluidAmount * 2;
                duration = (int) Math.round(duration * 1.15);
                minFruitAmount += 4;
                maxFruitAmount += 2;
            }

            if (gCropGenome.hasTrait("sprawling")) {
                fertilizerAmount = fertilizerAmount * 10;
                fluidAmount = fluidAmount * 10;
                duration = (int) Math.round(duration * 1.6);
                maxFruitAmount = maxFruitAmount * 8;
                minFruitAmount = minFruitAmount * 8;
            }

            if (gCropGenome.hasTrait("autotroph")) {
                fertilizerAmount = fertilizerAmount * 3;
                fluidAmount = fluidAmount * 3;
                duration = duration * 5;
                maxFruitAmount = maxFruitAmount * 4;
                minFruitAmount = minFruitAmount * 4;
            }

            int baseChance = 750 * cropTier;
            int chanceIncrease = 100 * cropTier;

            if (maxFruitAmount <= minFruitAmount) maxFruitAmount = minFruitAmount + 1;

            GTRecipeBuilder harvestRecipe = StarTRecipeTypes.GCROP_HARVESTER_RECIPES
                    .recipeBuilder(fruit.getId().getPath() + "_harvest")
                    .outputItemsRanged(new ItemStack(fruit.asItem()), UniformInt.of(minFruitAmount, maxFruitAmount))
                    .duration(duration)
                    .EUtVA(EUtV);

            if (cropTier == 0) {
                harvestRecipe.notConsumable(potentialCrop);
            } else {
                harvestRecipe.chancedInput(potentialCrop, baseChance, chanceIncrease);
            }

            if (fertilizerItem != null) harvestRecipe.inputItems(new ItemStack(fertilizerItem, fertilizerAmount));

            if (growthFluid != null) harvestRecipe.inputFluids(new FluidStack(growthFluid, fluidAmount));

            if (!gCropGenome.hasTrait("diurnal")) harvestRecipe.daytime(gCropGenome.hasTrait("nocturnal"));

            // StarTGCropGene climateGene = gCropGenome.getClimateGene();
            // StarTClimateType expectedClimate = StarTClimateType.getClimateFromTrait(climateGene.getTrait());
            // StarTClimateType actualClimateType = IClimateProvider.getClimateFromMachine(holder);

            // if (expectedClimate != actualClimateType) {
            // return null;
            // }

            return harvestRecipe.buildRawRecipe();
        }

        return null;
    }

    @Override
    public void buildRepresentativeRecipes() {
        final HashMap<Integer, Fluid> tieredGrowthFluids = new HashMap<>() {

            {
                put(0, GTMaterials.Water.getFluid());
                put(2, GTMaterials.Biomass.getFluid());
                put(4, GTMaterials.get("npk_solution").getFluid());
                put(5, GTMaterials.get("nutrient_rich_fertilizer_solution").getFluid());
                put(6, GTMaterials.get("biostimulating_mixture").getFluid());
            }
        };

        final HashMap<Integer, Item> tieredGrowthItems = new HashMap<>() {

            {
                put(1, Items.BONE_MEAL);
                put(3, GTItems.FERTILIZER.asItem());
                put(5, ChemicalHelper.get(TagPrefix.dust, GTMaterials.Phosphate).getItem());
                put(7, ChemicalHelper.get(TagPrefix.dust, GTMaterials.Strontium).getItem());
            }
        };

        for (ItemEntry<ComponentItem> crop : GCROP_ITEMS) {
            ItemStack gCrop = new ItemStack(crop.asItem());

            StarTCustomTooltipsManager.writeCustomTooltipsToItem(gCrop.getOrCreateTag(),
                    "behaviour.start_core.gcrop.harvester.gcrop",
                    "behaviour.start_core.gcrop.harvester.disclaimer");

            StarTGCropBehaviour cropBehaviour = StarTGCropBehaviour.getGCropBehaviour(gCrop);
            if (cropBehaviour == null) continue;

            ItemEntry<ComponentItem> fruit = GCROP_FRUITMAP.get(cropBehaviour.getCropMaterial());
            ItemStack fruitItem = new ItemStack(fruit.asItem());

            StarTCustomTooltipsManager.writeCustomTooltipsToItem(fruitItem.getOrCreateTag(),
                    "behaviour.start_core.gcrop.harvester.fruit");

            int cropTier = cropBehaviour.getCropTier();

            Item fertilizerItem = null;
            int foundTierItem = 0;
            for (int j = cropTier; j >= 0; j--) {
                if (tieredGrowthItems.containsKey(j)) {
                    fertilizerItem = tieredGrowthItems.get(j);
                    foundTierItem = j;
                    break;
                }
            }
            int fertilizerAmount = 1 << (2 * (cropTier - foundTierItem));

            Fluid growthFluid = null;
            int foundTierFluid = 0;
            for (int j = cropTier; j >= 0; j--) {
                if (tieredGrowthFluids.containsKey(j)) {
                    growthFluid = tieredGrowthFluids.get(j);
                    foundTierFluid = j;
                    break;
                }
            }
            int fluidAmount = 100 << (2 * (cropTier - foundTierFluid));

            GTRecipeBuilder harvestRecipe = StarTRecipeTypes.GCROP_HARVESTER_RECIPES
                    .recipeBuilder(fruit.getId().getPath() + "_harvest")
                    .outputItemsRanged(fruitItem, UniformInt.of(1, 4))
                    .duration((cropTier == 0) ? 160 : 160 * cropTier)
                    .daytime()
                    .EUtVA(StarTGCropItems.tierVoltages.get(cropTier));

            if (cropTier == 0) {
                harvestRecipe.notConsumable(gCrop);
            } else {
                harvestRecipe.chancedInput(gCrop, 750 * cropTier, 100 * cropTier);
            }

            if (fertilizerItem != null) {
                ItemStack fertilizerItemStack = new ItemStack(fertilizerItem, fertilizerAmount);
                StarTCustomTooltipsManager.writeCustomTooltipsToItem(fertilizerItemStack.getOrCreateTag(),
                        "behaviour.start_core.gcrop.harvester.fertilizer");

                harvestRecipe.inputItems(fertilizerItemStack);
            }

            if (growthFluid != null) {
                FluidStack growthFluidStack = new FluidStack(growthFluid, fluidAmount);
                StarTCustomTooltipsManager.writeCustomTooltipsToItem(growthFluidStack.getOrCreateTag(),
                        "behaviour.start_core.gcrop.harvester.fluid");

                harvestRecipe.inputFluids(growthFluidStack);
            }

            StarTCustomLogicUtils.handleCustomRecipeLogicEMI(
                    StarTRecipeTypes.GCROP_HARVESTER_RECIPES, "gcrops", harvestRecipe.buildRawRecipe());
        }
    }
}
