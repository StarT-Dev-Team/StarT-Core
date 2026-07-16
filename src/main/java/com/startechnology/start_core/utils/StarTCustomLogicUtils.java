package com.startechnology.start_core.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeCategories;

public class StarTCustomLogicUtils {

    public static List<NotifiableItemStackHandler> getItemHandlers(IRecipeCapabilityHolder holder) {
        return Objects
                .requireNonNullElseGet(holder.getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP),
                        Collections::emptyList)
                .stream()
                .filter(NotifiableItemStackHandler.class::isInstance)
                .map(NotifiableItemStackHandler.class::cast)
                .filter(i -> i.getSlots() >= 1)
                .toList();
    }

    public static List<NotifiableFluidTank> getFluidHandlers(IRecipeCapabilityHolder holder) {
        return Objects
                .requireNonNullElseGet(holder.getCapabilitiesFlat(IO.IN, FluidRecipeCapability.CAP),
                        Collections::emptyList)
                .stream()
                .filter(NotifiableFluidTank.class::isInstance)
                .map(NotifiableFluidTank.class::cast)
                .filter(i -> i.getTanks() >= 1)
                .toList();
    }

    public static Map<Boolean, List<NotifiableItemStackHandler>> getItemHandlersMap(IRecipeCapabilityHolder holder) {
        return Objects
                .requireNonNullElseGet(holder.getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP),
                        Collections::emptyList)
                .stream()
                .filter(NotifiableItemStackHandler.class::isInstance)
                .map(NotifiableItemStackHandler.class::cast)
                .filter(i -> i.getSlots() >= 1)
                .collect(Collectors.groupingBy(NotifiableRecipeHandlerTrait::isDistinct));
    }

    public static Map<Boolean, List<NotifiableFluidTank>> getFluidHandlersMap(IRecipeCapabilityHolder holder) {
        return Objects
                .requireNonNullElseGet(holder.getCapabilitiesFlat(IO.IN, FluidRecipeCapability.CAP),
                        Collections::emptyList)
                .stream()
                .filter(NotifiableFluidTank.class::isInstance)
                .map(NotifiableFluidTank.class::cast)
                .filter(i -> i.getTanks() >= 1)
                .collect(Collectors.groupingBy(NotifiableRecipeHandlerTrait::isDistinct));
    }

    public static @Nullable GTRecipe createCustomlogicRecipeWithItemHandlers(List<NotifiableItemStackHandler> handlers,
                                                                             Function<NotifiableItemStackHandler, GTRecipe> recipeCreationMethod) {
        if (handlers.isEmpty()) return null;

        for (var handler : handlers) {
            GTRecipe recipe = recipeCreationMethod.apply(handler);
            if (recipe != null) return recipe;
        }

        return null;
    }

    public static @Nullable GTRecipe createCustomlogicRecipeWithFluidHandlers(List<NotifiableFluidTank> handlers,
                                                                              Function<NotifiableFluidTank, GTRecipe> recipeCreationMethod) {
        if (handlers.isEmpty()) return null;

        for (var handler : handlers) {
            GTRecipe recipe = recipeCreationMethod.apply(handler);
            if (recipe != null) return recipe;
        }

        return null;
    }

    public static void handleCustomRecipeLogicEMI(@NotNull GTRecipeType recipeType, @NotNull String categoryId,
                                                  @NotNull GTRecipe recipe) {
        // for EMI to detect it's a synthetic recipe (not ever in JSON)
        recipe.setId(recipe.getId().withPrefix("/"));
        recipeType.addToCategoryMap(GTRecipeCategories.get(categoryId), recipe);
    }

    public static void handleCustomRecipeLogicEMI(@NotNull GTRecipeType recipeType, @NotNull GTRecipe recipe) {
        // for EMI to detect it's a synthetic recipe (not ever in JSON)
        recipe.setId(recipe.getId().withPrefix("/"));
        recipeType.addToMainCategory(recipe);
    }
}
