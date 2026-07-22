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
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.machine.ore_factory.OreFactoryMachine;
import com.startechnology.start_core.recipe.StarTRecipeTypes;

import net.minecraftforge.fluids.FluidStack;

public class OreFactoryLogic implements ICustomRecipeLogic {

    private static final int BASE_DURATION = 100;
    private static final long BASE_EUT = GTValues.V[GTValues.LV];

    @Override
    public void buildRepresentativeRecipes() {
        OreFactoryMachine.FLUID_STATS.forEach(
                (material, stats) -> {
                    FluidStack fluidInput = material.getFluid(stats.amount);

                    int duration = (int) Math.round(BASE_DURATION * stats.durationMultiplier);
                    long eut = (long) Math.round(BASE_EUT * stats.euMultiplier);

                    StarTCustomTooltipsManager.writeCustomTooltipsToItem(
                            fluidInput.getOrCreateTag(),
                            LocalizationUtils.format("behaviour.start_core.orefactory.duration", String.format("%.2f", stats.durationMultiplier)),
                            LocalizationUtils.format("behaviour.start_core.orefactory.eut", String.format("%.2f", stats.euMultiplier))
                    );

                    GTRecipe oreFactoryRecipe = StarTRecipeTypes.ORE_FACTORY_RECIPE
                            .recipeBuilder(material.getName() + "_ore_factory")
                            .inputFluids(fluidInput)
                            .duration(duration)
                            .EUt(eut)
                            .buildRawRecipe();

                    // for EMI to detect it's a synthetic recipe (not ever in JSON)
                    oreFactoryRecipe.setId(oreFactoryRecipe.getId().withPrefix("/"));
                    StarTRecipeTypes.ORE_FACTORY_RECIPE.addToMainCategory(oreFactoryRecipe);
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
            GTRecipe recipe = createOreFactoryRecipe(handler);
            if (recipe != null) return recipe;
        }

        return null;
    }

    private GTRecipe createOreFactoryRecipe(NotifiableFluidTank handler) {
        for (int i = 0; i < handler.getTanks(); ++i) {
            FluidStack fluidInSlot = handler.getFluidInTank(i);

            if (fluidInSlot == null) continue;

            if (!fluidInSlot.isEmpty()) {
                Material fluidMaterial = ChemicalHelper.getMaterial(fluidInSlot.getFluid());
                if (fluidMaterial == null) continue;

                OreFactoryMachine.FluidStats stats = OreFactoryMachine.FLUID_STATS.get(fluidMaterial);
                if (stats != null) {
                    FluidStack fluidInput = fluidInSlot.copy();
                    fluidInput.setAmount(stats.amount);

                    int duration = (int) Math.round(BASE_DURATION * stats.durationMultiplier);
                    long eut = (long) Math.round(BASE_EUT * stats.euMultiplier);

                    return StarTRecipeTypes.ORE_FACTORY_RECIPE
                            .recipeBuilder("ore_factory")
                            .inputFluids(fluidInput)
                            .duration(duration)
                            .EUt(eut)
                            .buildRawRecipe();
                }
            }
        }

        return null;
    }
}