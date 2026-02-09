package com.startechnology.start_core.recipe.logic;

import static com.startechnology.start_core.item.StarTBacteriaItems.BACTERIA_DORMANT;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
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
import com.tterrag.registrate.util.entry.ItemEntry;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

public class BacterialDormantAwakeningLogic implements ICustomRecipeLogic {
    @Override
    public void buildRepresentativeRecipes() {
        ItemStack bacteriaDormantMutation = new ItemStack(BACTERIA_DORMANT.asItem()); 
        StarTCustomTooltipsManager.writeCustomTooltipsToItem(bacteriaDormantMutation.getOrCreateTag(), 
            "behaviour.start_core.bacteria.mutator_total_output"
        );

        bacteriaDormantMutation.setHoverName(Component.translatable(
            "behaviour.start_core.bacteria.mutator_total_output_generic_bacteria"
        ));
        
        ItemStack netherstar = new ItemStack(ForgeRegistries.ITEMS.getValue(Minecraft.id("nether_star")));

        GTRecipe dormantRecipe = StarTRecipeTypes.BACTERIAL_RUNIC_MUTATOR_RECIPES
            .recipeBuilder("dormant_awakening")
            .inputItems(new ItemStack(BACTERIA_DORMANT.asItem()))
            .inputItems(netherstar)
            .inputFluids(GTMaterials.DistilledWater.getFluid(32000))
            .inputFluids(GTMaterials.Naquadria.getFluid(8000))
            .outputItems(bacteriaDormantMutation)
            .duration(1200)
            .EUt(GTValues.V[GTValues.UV])
            .buildRawRecipe();

        // for EMI to detect it's a synthetic recipe (not ever in JSON)
        dormantRecipe.setId(dormantRecipe.getId().withPrefix("/"));
        StarTRecipeTypes.BACTERIAL_RUNIC_MUTATOR_RECIPES.addToMainCategory(dormantRecipe);
    }

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
                List<NotifiableItemStackHandler> handlers = Objects
                .requireNonNullElseGet(holder.getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP),
                        Collections::emptyList)
                .stream()
                .filter(NotifiableItemStackHandler.class::isInstance)
                .map(NotifiableItemStackHandler.class::cast)
                .filter(i -> i.getSlots() >= 1)
                .collect(Collectors.toList());
    
        if (handlers.isEmpty()) return null;

        // Return for the first recipe found
        for (NotifiableItemStackHandler handler : handlers) {
            GTRecipe recipe = createDormantAwakeningRecipe(handler);
            if (recipe != null) return recipe;
        }

        return null;
    }

    private GTRecipe createDormantAwakeningRecipe(NotifiableItemStackHandler handler) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack itemInSlot = handler.getStackInSlot(i);
            
            if (itemInSlot == null) continue;

            if (!itemInSlot.isEmpty()) {
                Integer production = StarTCore.RNG.nextIntBetweenInclusive(1,  StarTBacteriaStats.MAX_STAT_VALUE);
                Integer metabolism = StarTCore.RNG.nextIntBetweenInclusive(1,  StarTBacteriaStats.MAX_STAT_VALUE);
                Integer mutability = StarTCore.RNG.nextIntBetweenInclusive(1,  StarTBacteriaStats.MAX_STAT_VALUE);    

                ItemEntry<ComponentItem> nextType = StarTBacteriaItems.BACTERIA_ITEMS.get(
                    StarTCore.RNG.nextIntBetweenInclusive(0, StarTBacteriaItems.BACTERIA_ITEMS.size() - 1)
                );

                ItemStack output = new ItemStack(nextType.get());
                List<Fluid> possibleNewAffinities = StarTBacteriaBehaviour.getBacteriaBehaviour(output).getBehaviourAffinityFluids();
                Collections.shuffle(possibleNewAffinities);

                StarTBacteriaStats newStats = new StarTBacteriaStats(
                    production, metabolism, mutability, 
                    possibleNewAffinities.get(0), possibleNewAffinities.get(1), possibleNewAffinities.get(2)
                );
                
                StarTBacteriaManager.writeBacteriaStatsToItem(output.getOrCreateTag(), newStats);

                ItemStack runic = new ItemStack(ForgeRegistries.ITEMS.getValue(KubeJS.id("runic_engraved_plating")));

                // Output
                return StarTRecipeTypes.BACTERIAL_RUNIC_MUTATOR_RECIPES
                    .recipeBuilder("runic_mutator_dormant")
                    .inputItems(new ItemStack(BACTERIA_DORMANT.asItem()))
                    .inputItems(runic)
                    .inputFluids(GTMaterials.DistilledWater.getFluid(32000))
                    .inputFluids(GTMaterials.Mutagen.getFluid( 8000))
                    .outputItems(output)
                    .duration(480)
                    .EUt(GTValues.V[GTValues.UV])
                    .buildRawRecipe();
            }
        }

        return null;
    }
}
