package com.startechnology.start_core.recipe.logic;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachine;
import com.startechnology.start_core.materials.StarTHellForgeHeatingLiquids;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTCustomLogicUtils;

import org.jetbrains.annotations.Nullable;

import net.minecraftforge.fluids.FluidStack;

public class HellForgeHeatingLogic implements ICustomRecipeLogic {

    @Override
    public void buildRepresentativeRecipes() {
        StarTHellForgeMachine.fluidsMap.forEach(
                (material, heat) -> {
                    FluidStack heatingFluidInput = material.getFluid(1000);

                    int temperature = heatingFluidInput.getFluid().getFluidType().getTemperature();

                    StarTCustomTooltipsManager.writeCustomTooltipsToItem(
                            heatingFluidInput.getOrCreateTag(),
                            LocalizationUtils.format("behaviour.start_core.hellforge.input_heat",
                                    FormattingUtil.formatNumbers(temperature / 1_000_000)),
                            LocalizationUtils.format("behaviour.start_core.hellforge.max_heat",
                                    FormattingUtil.formatNumbers(heat)));

                    GTRecipe heatingRecipe = StarTRecipeTypes.HELL_FORGE_RECIPES
                            .recipeBuilder(material.getName() + "_hellforge_heating")
                            .inputFluids(heatingFluidInput)
                            .outputFluids(StarTHellForgeHeatingLiquids.InfernalTar.getFluid(500))
                            .duration(64)
                            .EUt(GTValues.V[GTValues.UEV])
                            .buildRawRecipe();

                    StarTCustomLogicUtils.handleCustomRecipeLogicEMI(StarTRecipeTypes.HELL_FORGE_RECIPES,
                            "hellforge_heating", heatingRecipe);
                });
    }

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        var handlers = StarTCustomLogicUtils.getFluidHandlers(holder);

        return StarTCustomLogicUtils.createCustomlogicRecipeWithFluidHandlers(handlers, this::createHeatingRecipe);
    }

    private GTRecipe createHeatingRecipe(NotifiableFluidTank handler) {
        for (int i = 0; i < handler.getTanks(); ++i) {
            FluidStack fluidInSlot = handler.getFluidInTank(i);

            if (!fluidInSlot.isEmpty()) {
                Material fluidMaterial = ChemicalHelper.getMaterial(fluidInSlot.getFluid());

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
