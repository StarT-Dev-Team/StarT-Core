package com.startechnology.start_core.recipe.logic.bacteria;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.bacteria.StarTBacteriaManager;
import com.startechnology.start_core.api.bacteria.StarTBacteriaStats;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.item.StarTBacteriaItems;
import com.startechnology.start_core.item.components.StarTBacteriaBehaviour;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import com.tterrag.registrate.util.entry.ItemEntry;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;

import java.util.Collections;
import java.util.List;

import static com.startechnology.start_core.item.StarTBacteriaItems.BACTERIA_DORMANT;

public class BacterialDormantAwakeningLogic implements ICustomRecipeLogic {

    @Override
    public void buildRepresentativeRecipes() {
        ItemStack bacteriaDormantMutation = new ItemStack(BACTERIA_DORMANT.asItem());
        StarTCustomTooltipsManager.writeCustomTooltipsToItem(bacteriaDormantMutation.getOrCreateTag(),
                "behaviour.start_core.bacteria.mutator_total_output");

        bacteriaDormantMutation.setHoverName(Component.translatable(
                "behaviour.start_core.bacteria.mutator_total_output_generic_bacteria"));

        ItemStack netherStar = new ItemStack(Items.NETHER_STAR);

        GTRecipe dormantRecipe = StarTRecipeTypes.BACTERIAL_RUNIC_MUTATOR_RECIPES
                .recipeBuilder("dormant_awakening")
                .inputItems(new ItemStack(BACTERIA_DORMANT.asItem()))
                .inputItems(netherStar)
                .inputFluids(GTMaterials.DistilledWater.getFluid(32000))
                .inputFluids(GTMaterials.Naquadria.getFluid(8000))
                .outputItems(bacteriaDormantMutation)
                .duration(1200)
                .EUt(GTValues.V[GTValues.UV])
                .buildRawRecipe();

        StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.BACTERIAL_RUNIC_MUTATOR_RECIPES, dormantRecipe);
    }

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var handlers = StarTCustomLogicUtils.getItemHandlers(holder);

        return StarTCustomLogicUtils.createCustomlogicRecipeWithItemHandlers(handlers,
                this::createDormantAwakeningRecipe);
    }

    private GTRecipe createDormantAwakeningRecipe(NotifiableItemStackHandler handler) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack itemInSlot = handler.getStackInSlot(i);

            if (!itemInSlot.isEmpty()) {
                int production = StarTCore.RNG.nextIntBetweenInclusive(1, StarTBacteriaStats.MAX_STAT_VALUE);
                int metabolism = StarTCore.RNG.nextIntBetweenInclusive(1, StarTBacteriaStats.MAX_STAT_VALUE);
                int mutability = StarTCore.RNG.nextIntBetweenInclusive(1, StarTBacteriaStats.MAX_STAT_VALUE);

                ItemEntry<ComponentItem> nextType = StarTBacteriaItems.BACTERIA_ITEMS.get(
                        StarTCore.RNG.nextIntBetweenInclusive(0, StarTBacteriaItems.BACTERIA_ITEMS.size() - 1));

                ItemStack output = new ItemStack(nextType.get());
                List<Fluid> possibleNewAffinities = StarTBacteriaBehaviour.getBacteriaBehaviour(output)
                        .getBehaviourAffinityFluids();
                Fluid superFluid = StarTBacteriaBehaviour.getBacteriaBehaviour(output).getSuperfluid().getFluid();
                Collections.shuffle(possibleNewAffinities);

                StarTBacteriaStats newStats = new StarTBacteriaStats(
                        production, metabolism, mutability,
                        possibleNewAffinities.get(0), possibleNewAffinities.get(1), possibleNewAffinities.get(2),
                        superFluid);

                StarTBacteriaManager.writeBacteriaStatsToItem(output.getOrCreateTag(), newStats);

                ItemStack netherStar = new ItemStack(Items.NETHER_STAR);

                // Output
                return StarTRecipeTypes.BACTERIAL_RUNIC_MUTATOR_RECIPES
                        .recipeBuilder("runic_mutator_dormant")
                        .inputItems(new ItemStack(BACTERIA_DORMANT.asItem()))
                        .inputItems(netherStar)
                        .inputFluids(GTMaterials.DistilledWater.getFluid(32000))
                        .inputFluids(GTMaterials.Naquadria.getFluid(8000))
                        .outputItems(output)
                        .duration(1200)
                        .EUt(GTValues.V[GTValues.UV])
                        .buildRawRecipe();
            }
        }

        return null;
    }
}
