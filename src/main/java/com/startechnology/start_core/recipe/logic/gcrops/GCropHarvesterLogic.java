package com.startechnology.start_core.recipe.logic.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.startechnology.start_core.api.gcrop.*;
import com.startechnology.start_core.data.gcrops.StarTGCropTraits;
import com.startechnology.start_core.item.StarTGCropItems;
import com.startechnology.start_core.item.components.StarTGCropBehaviour;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.startechnology.start_core.data.gcrops.StarTGCropTraits.TRAIT_COMPARATOR;
import static com.startechnology.start_core.item.StarTGCropItems.GCROP_FRUITMAP;

public class GCropHarvesterLogic implements ICustomRecipeLogic {

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var handlers = StarTCustomLogicUtils.getItemHandlers(holder);

        return StarTCustomLogicUtils.createCustomlogicRecipeWithItemHandlers(handlers, this::createHarvesterRecipe);
    }

    private GTRecipe createHarvesterRecipe(NotifiableItemStackHandler handler) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack itemInSlot = handler.getStackInSlot(i);

            if (itemInSlot.isEmpty()) continue;

            StarTGCropBehaviour cropBehaviour = StarTGCropBehaviour.getGCropBehaviour(itemInSlot);
            if (cropBehaviour == null) continue;

            StarTGCropGenome gCropGenomes = StarTGCropManager.gcropGenomeFromTag(itemInSlot);
            assert gCropGenomes != null;

            List<StarTGCropTraits.StarTGCropTrait> allResourceTraits = new ArrayList<>();

            for (StarTGCropGene gene : gCropGenomes.getResourceGenome()) {
                allResourceTraits.add(gene.getTrait());
            }

            allResourceTraits.sort(TRAIT_COMPARATOR);

            ItemEntry<ComponentItem> gCropItem = StarTGCropItems.getGCropByGenome(allResourceTraits);
            var fruit = GCROP_FRUITMAP.get(gCropItem);

            if (fruit == null) continue;

            ItemStack gCropFruit = new ItemStack(fruit.asItem());

            return StarTRecipeTypes.GCROP_HARVESTER_RECIPES
                    .recipeBuilder(fruit.getId().getPath() + "_harvest")
                    .inputItems(itemInSlot.copyWithCount(1))
                    .inputItems(new ItemStack(GTItems.FERTILIZER).copyWithCount(1))
                    .outputItems(gCropFruit.copyWithCount(1))
                    .duration(160)
                    .EUtV(GTValues.MV)
                    .buildRawRecipe();
        }

        return null;
    }

    @Override
    public void buildRepresentativeRecipes() {
        GCROP_FRUITMAP.keySet().forEach(
                crop -> {
                    var fruit = GCROP_FRUITMAP.get(crop);

                    ItemStack gCrop = new ItemStack(crop.asItem());
                    ItemStack gCropFruit = new ItemStack(fruit.asItem());

                    GTRecipe harvesterRecipe = StarTRecipeTypes.GCROP_HARVESTER_RECIPES
                            .recipeBuilder(fruit.getId().getPath() + "_harvest")
                            .inputItems(gCrop.copyWithCount(1))
                            .inputItems(new ItemStack(GTItems.FERTILIZER).copyWithCount(1))
                            .outputItems(gCropFruit.copyWithCount(1))
                            .duration(160)
                            .EUtV(GTValues.MV)
                            .buildRawRecipe();

                    StarTCustomLogicUtils.handleCustomRecipeLogicEMI(
                            StarTRecipeTypes.GCROP_HARVESTER_RECIPES, "harvesting", harvesterRecipe);
                });
    }
}
