package com.startechnology.start_core.recipe.logic.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.startechnology.start_core.api.gcrop.*;
import com.startechnology.start_core.item.components.StarTGCropBehaviour;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

import static com.startechnology.start_core.item.StarTGCropItems.GCROP_FRUITMAP;
import static com.startechnology.start_core.item.StarTGCropItems.GCROP_ITEMS;

public class GCropHarvesterLogic implements ICustomRecipeLogic {

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var handlers = StarTCustomLogicUtils.getItemHandlers(holder);

        return StarTCustomLogicUtils.createCustomlogicRecipeWithItemHandlers(handlers, this::createHarvesterRecipe);
    }

    private GTRecipe createHarvesterRecipe(NotifiableItemStackHandler handler) {
        final HashMap<Integer, Fluid> tieredGrowthFluids = new HashMap<>() {

            {
                put(1, GTMaterials.Water.getFluid());
            }
        };

        final HashMap<Integer, Item> tieredGrowthItems = new HashMap<>() {

            {
                put(0, GTItems.FERTILIZER.asItem());
            }
        };

        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack itemInSlot = handler.getStackInSlot(i);

            if (itemInSlot.isEmpty()) continue;

            StarTGCropBehaviour cropBehaviour = StarTGCropBehaviour.getGCropBehaviour(itemInSlot);

            if (cropBehaviour == null) continue;

            StarTGCropGenome gCropGenome = StarTGCropManager.gcropGenomeFromTag(itemInSlot);

            if (gCropGenome == null) continue;

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
            int fertilizerAmount = 1 << (cropTier - foundTierItem);
            assert fertilizerItem != null;

            Fluid growthFluid = null;
            int foundTierFluid = 0;
            for (int j = cropTier; j >= 0; j--) {
                if (tieredGrowthFluids.containsKey(j)) {
                    growthFluid = tieredGrowthFluids.get(j);
                    foundTierFluid = j;
                    break;
                }
            }
            int fluidAmount = 100 << foundTierFluid;

            int fruitAmount = 1;

            ItemEntry<ComponentItem> fruit = GCROP_FRUITMAP.get(cropBehaviour.getCropMaterial());

            if (fruit == null) continue;

            if (gCropGenome.hasTrait("speedy")) duration = (int) Math.round(duration * 0.9);
            if (gCropGenome.hasTrait("dry")) fluidAmount = (int) Math.round(fluidAmount * 1.2);

            GTRecipeBuilder harvestRecipe = StarTRecipeTypes.GCROP_HARVESTER_RECIPES
                    .recipeBuilder(fruit.getId().getPath() + "_harvest")
                    .inputItems(itemInSlot.copyWithCount(1))
                    .inputItems(new ItemStack(fertilizerItem, fertilizerAmount))
                    .outputItems(new ItemStack(fruit.asItem(), fruitAmount))
                    .duration(duration)
                    .EUtVA(EUt);

            if (growthFluid != null) {
                if (gCropGenome.hasTrait("x")) duration = 1;
                else harvestRecipe.inputFluids(new FluidStack(growthFluid, fluidAmount));
            }

            if (!gCropGenome.hasTrait("diurnal")) harvestRecipe.daytime(gCropGenome.hasTrait("nocturnal"));

            return harvestRecipe.buildRawRecipe();
        }

        return null;
    }

    @Override
    public void buildRepresentativeRecipes() {
        for (var crop : GCROP_ITEMS) {
            ItemStack gCrop = new ItemStack(crop.asItem());

            var cropBehaviour = StarTGCropBehaviour.getGCropBehaviour(gCrop);
            if (cropBehaviour == null) continue;

            var fruit = GCROP_FRUITMAP.get(cropBehaviour.getCropMaterial());
            ItemStack gCropFruit = new ItemStack(fruit.asItem());

            GTRecipe harvesterRecipe = StarTRecipeTypes.GCROP_HARVESTER_RECIPES
                    .recipeBuilder(fruit.getId().getPath() + "_harvest")
                    .inputItems(gCrop)
                    .inputItems(new ItemStack(GTItems.FERTILIZER))
                    .outputItems(gCropFruit)
                    .duration(160)
                    .EUtV(GTValues.MV)
                    .buildRawRecipe();

            StarTCustomLogicUtils.handleCustomRecipeLogicEMI(
                    StarTRecipeTypes.GCROP_HARVESTER_RECIPES, harvesterRecipe);
        }
    }
}
