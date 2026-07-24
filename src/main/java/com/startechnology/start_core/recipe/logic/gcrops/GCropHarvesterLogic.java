package com.startechnology.start_core.recipe.logic.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
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
import com.startechnology.start_core.api.gcrop.*;
import com.startechnology.start_core.item.components.StarTGCropBehaviour;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

import static com.startechnology.start_core.item.StarTGCropItems.*;

public class GCropHarvesterLogic implements ICustomRecipeLogic {

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var itemHandlers = StarTCustomLogicUtils.getItemHandlersMap(holder);
        if (itemHandlers.isEmpty()) return null;

        List<List<ItemStack>> allItems = StarTCustomLogicUtils.getAllItems(itemHandlers);

        for (List<ItemStack> itemSet : allItems) {
            GTRecipe recipe = createHarvesterRecipe(itemSet);
            if (recipe != null) return recipe;
        }

        return null;
    }

    private GTRecipe createHarvesterRecipe(List<ItemStack> itemSet) {
        final HashMap<Integer, Fluid> tieredGrowthFluids = new HashMap<>() {

            {
                put(0, GTMaterials.Water.getFluid());
                put(2, GTMaterials.Biomass.getFluid());
                put(4, GTMaterials.get("nutrient_rich_fertilizer_solution").getFluid());
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

        for (ItemStack item : itemSet) {
            StarTGCropBehaviour cropBehaviour = StarTGCropBehaviour.getGCropBehaviour(item);
            if (cropBehaviour == null) continue;

            StarTGCropGenome gCropGenome = StarTGCropManager.gcropGenomeFromTag(item);
            if (gCropGenome == null) continue;

            ItemEntry<ComponentItem> fruit = GCROP_FRUITMAP.get(cropBehaviour.getCropMaterial());
            if (fruit == null) continue;

            int cropTier = cropBehaviour.getCropTier();

            int duration = (cropTier == 0) ? 160 : 160 * cropTier;
            if (gCropGenome.hasTrait("quickened")) duration = (int) Math.round(duration * 0.9);
            if (gCropGenome.hasTrait("speedy")) duration = (int) Math.round(duration * 0.9);
            if (gCropGenome.hasTrait("fast")) duration = (int) Math.round(duration * 0.9);

            if (gCropGenome.hasTrait("slow")) duration = (int) Math.round(duration * 1.2);

            if (gCropGenome.hasTrait("early")) {
                duration = (int) Math.round(duration * 0.7);
                ItemEntry<ComponentItem> flower = GCROP_FLOWERMAP.get(cropBehaviour.getCropMaterial());
                if (flower != null) fruit = flower;
            }

            int EUtV = GTValues.MV + cropTier;
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
            // if (gCropGenome.hasTrait("dry")) fluidAmount = (int) Math.round(fluidAmount * 1.2);

            int fruitAmount = 1;
            if (gCropGenome.hasTrait("enormous")) {
                if (StarTCore.RNG.nextIntBetweenInclusive(1, 100) < 60) fruitAmount += 1;
            }

            if (gCropGenome.hasTrait("branching")) {
                for (int j = 0; j < 3; j++) if (StarTCore.RNG.nextIntBetweenInclusive(1, 100) < 70) fruitAmount += 1;
            }

            if (gCropGenome.hasTrait("proliferating")) {
                fertilizerAmount = fertilizerAmount * 2;
                fluidAmount = fluidAmount * 2;
                duration = (int) Math.round(duration * 1.15);
                fruitAmount += 3;
            }

            if (gCropGenome.hasTrait("ancient")) {
                fertilizerAmount = fertilizerAmount * 4;
                fluidAmount = fluidAmount * 4;
                duration = (int) Math.round(duration * 1.6);
                fruitAmount = fruitAmount * 8;
            }

            GTRecipeBuilder harvestRecipe = StarTRecipeTypes.GCROP_HARVESTER_RECIPES
                    .recipeBuilder(fruit.getId().getPath() + "_harvest")
                    .inputItems(item.copyWithCount(1))
                    // .outputItemsRanged(new ItemStack(fruit.asItem(), fruitAmount), )
                    .outputItems(new ItemStack(fruit.asItem(), fruitAmount))
                    .duration(duration)
                    .EUtVA(EUtV);

            if (fertilizerItem != null) harvestRecipe.inputItems(new ItemStack(fertilizerItem, fertilizerAmount));

            if (growthFluid != null) harvestRecipe.inputFluids(new FluidStack(growthFluid, fluidAmount));

            if (!gCropGenome.hasTrait("diurnal")) harvestRecipe.daytime(gCropGenome.hasTrait("nocturnal"));

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
                put(4, GTMaterials.get("nutrient_rich_fertilizer_solution").getFluid());
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

        for (var crop : GCROP_ITEMS) {
            ItemStack gCrop = new ItemStack(crop.asItem());

            var cropBehaviour = StarTGCropBehaviour.getGCropBehaviour(gCrop);
            if (cropBehaviour == null) continue;

            var fruit = GCROP_FRUITMAP.get(cropBehaviour.getCropMaterial());

            int cropTier = cropBehaviour.getCropTier();
            int duration = (cropTier == 0) ? 160 : 160 * cropTier;
            int EUt = GTValues.MV + cropTier;

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

            int fruitAmount = 1;

            GTRecipeBuilder harvestRecipe = StarTRecipeTypes.GCROP_HARVESTER_RECIPES
                    .recipeBuilder(fruit.getId().getPath() + "_harvest")
                    .inputItems(gCrop.copyWithCount(1))
                    .outputItems(new ItemStack(fruit.asItem(), fruitAmount))
                    .duration(duration)
                    .daytime()
                    .EUtVA(EUt);

            if (fertilizerItem != null) harvestRecipe.inputItems(new ItemStack(fertilizerItem, fertilizerAmount));

            if (growthFluid != null) harvestRecipe.inputFluids(new FluidStack(growthFluid, fluidAmount));

            StarTCustomLogicUtils.handleCustomRecipeLogicEMI(
                    StarTRecipeTypes.GCROP_HARVESTER_RECIPES, "gcrops", harvestRecipe.buildRawRecipe());
        }
    }
}
