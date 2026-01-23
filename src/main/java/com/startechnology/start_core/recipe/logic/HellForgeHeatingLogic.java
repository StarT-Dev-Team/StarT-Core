package com.startechnology.start_core.recipe.logic;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeCategories;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import com.startechnology.start_core.api.bacteria.StarTBacteriaManager;
import com.startechnology.start_core.api.bacteria.StarTBacteriaStats;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.item.components.StarTBacteriaBehaviour;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachine;
import com.startechnology.start_core.materials.StarTHellForgeHeatingLiquids;
import com.startechnology.start_core.recipe.StarTRecipeCategories;
import com.startechnology.start_core.recipe.StarTRecipeTypes;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class HellForgeHeatingLogic implements ICustomRecipeLogic {
    @Override
    public void buildRepresentativeRecipes() {
        StarTHellForgeMachine.fluidsMap.forEach(
            (material, heat) -> {
                    FluidStack heatingFluidInput = material.getFluid(1000);

                    Integer temperature = heatingFluidInput.getFluid().getFluidType().getTemperature();

                    StarTCustomTooltipsManager.writeCustomTooltipsToItem(
                        heatingFluidInput.getOrCreateTag(), 
                        LocalizationUtils.format("behaviour.start_core.hellforge.input_heat", FormattingUtil.formatNumbers(temperature / 1_000_000)),
                        LocalizationUtils.format("behaviour.start_core.hellforge.max_heat", FormattingUtil.formatNumbers(heat))
                    );

                    GTRecipe heatingRecipe = StarTRecipeTypes.HELL_FORGE_RECIPES
                        .recipeBuilder(material.getName() + "_hellforge_heating")
                        .inputFluids(heatingFluidInput)
                        .outputFluids(StarTHellForgeHeatingLiquids.InfernalTar.getFluid(500))
                        .duration(64)
                        .EUt(GTValues.V[GTValues.UEV])
                        .buildRawRecipe();
                
                    heatingRecipe.setId(heatingRecipe.getId().withPrefix("/"));
                    StarTRecipeTypes.HELL_FORGE_RECIPES.addToCategoryMap(GTRecipeCategories.get("hellforge_heating"), heatingRecipe);
            }
        );
    }

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        List<NotifiableFluidTank> handlers = Objects
            .requireNonNullElseGet(holder.getCapabilitiesFlat(IO.IN, FluidRecipeCapability.CAP),
                    Collections::emptyList)
            .stream()
            .filter(NotifiableFluidTank.class::isInstance)
            .map(NotifiableFluidTank.class::cast)
            .filter(i -> i.getTanks() >= 1)
            .collect(Collectors.toList());

        if (handlers.isEmpty()) return null;

        // Return for the first recipe found
        for (NotifiableFluidTank handler : handlers) {
            GTRecipe recipe = createHeatingRecipe(handler);
            if (recipe != null) return recipe;
        }

        return null;
    }

    private GTRecipe createHeatingRecipe(NotifiableFluidTank handler) {
        for (int i = 0; i < handler.getTanks(); ++i) {
            FluidStack fluidInSlot = handler.getFluidInTank(i);
            
            if (fluidInSlot == null) continue;

            if (!fluidInSlot.isEmpty()) {
                Material fluidMaterial = ChemicalHelper.getMaterial(fluidInSlot.getFluid());
                if (fluidMaterial == null) continue;

                if (StarTHellForgeMachine.fluidsMap.containsKey(fluidMaterial)) {
                    FluidStack fluidInput = fluidInSlot.copy();
                    fluidInput.setAmount(1000);

                    return StarTRecipeTypes.HELL_FORGE_RECIPES
                        .recipeBuilder("heating")
                        .inputFluids(fluidInput)
                        .outputFluids(StarTHellForgeHeatingLiquids.InfernalTar.getFluid(500))
                        .duration(64)
                        .EUt(GTValues.V[GTValues.UEV])
                        .buildRawRecipe();
                }
            }
        }

        return null;
    }
}
