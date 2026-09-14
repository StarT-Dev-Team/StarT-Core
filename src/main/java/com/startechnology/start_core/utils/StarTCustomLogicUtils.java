package com.startechnology.start_core.utils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.gregtechceu.gtceu.api.recipe.category.GTRecipeCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
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

import static com.startechnology.start_core.StarTCore.LOGGER;

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

    public static List<ItemStack> getAllItems(List<NotifiableItemStackHandler> itemHandlers) {
        List<ItemStack> allItems = new ArrayList<>();

        for (var itemHandler : itemHandlers) {
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                ItemStack itemInSlot = itemHandler.getStackInSlot(i);
                if (!itemInSlot.isEmpty()) allItems.add(itemInSlot);
            }
        }
        return allItems;
    }

    // overload for maps
    public static List<List<ItemStack>> getAllItems(Map<Boolean, List<NotifiableItemStackHandler>> itemHandlers) {
        List<ItemStack> allNonDistinctItems = new ArrayList<>();
        List<List<ItemStack>> allDistinctItemSets = new ArrayList<>();

        for (var itemHandler : itemHandlers.getOrDefault(false, Collections.emptyList())) {
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                ItemStack itemInSlot = itemHandler.getStackInSlot(i);
                if (!itemInSlot.isEmpty()) allNonDistinctItems.add(itemInSlot);
            }
        }

        for (var itemHandler : itemHandlers.getOrDefault(true, Collections.emptyList())) {
            List<ItemStack> distinctItemset = new ArrayList<>(allNonDistinctItems);
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                ItemStack itemInSlot = itemHandler.getStackInSlot(i);
                if (!itemInSlot.isEmpty()) distinctItemset.add(itemInSlot);
            }
            allDistinctItemSets.add(distinctItemset);
        }

        if (allDistinctItemSets.isEmpty()) allDistinctItemSets.add(allNonDistinctItems);
        return allDistinctItemSets;
    }

    public static List<FluidStack> getAllFluids(List<NotifiableFluidTank> fluidHandlers) {
        List<FluidStack> allFluids = new ArrayList<>();

        for (var fluidHandler : fluidHandlers) {
            for (int i = 0; i < fluidHandler.getTanks(); i++) {
                FluidStack fluidInSlot = fluidHandler.getFluidInTank(i);
                if (!fluidInSlot.isEmpty()) allFluids.add(fluidInSlot);
            }
        }
        return allFluids;
    }

    // overload for maps
    public static List<FluidStack> getAllFluids(Map<Boolean, List<NotifiableFluidTank>> fluidHandlers) {
        List<FluidStack> allFluids = new ArrayList<>();

        // How to properly account for distinct? This just ignores it
        for (var fluidHandler : fluidHandlers.getOrDefault(true, Collections.emptyList())) {
            for (int i = 0; i < fluidHandler.getTanks(); i++) {
                FluidStack fluidInSlot = fluidHandler.getFluidInTank(i);
                if (!fluidInSlot.isEmpty()) allFluids.add(fluidInSlot);
            }
        }
        for (var fluidHandler : fluidHandlers.getOrDefault(false, Collections.emptyList())) {
            for (int i = 0; i < fluidHandler.getTanks(); i++) {
                FluidStack fluidInSlot = fluidHandler.getFluidInTank(i);
                if (!fluidInSlot.isEmpty()) allFluids.add(fluidInSlot);
            }
        }
        return allFluids;
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

        GTRecipeCategory recipeCategory = GTRecipeCategories.get(categoryId);

        if (recipeCategory == null) {
            recipeType.addToMainCategory(recipe);
            LOGGER.debug("Could not find recipe category for recipe type: " + recipeType + "; category: " + categoryId);
        } else recipeType.addToCategoryMap(recipeCategory, recipe);
    }

    public static void handleCustomRecipeLogicEMI(@NotNull GTRecipeType recipeType, @NotNull GTRecipe recipe) {
        // for EMI to detect it's a synthetic recipe (not ever in JSON)
        recipe.setId(recipe.getId().withPrefix("/"));
        recipeType.addToMainCategory(recipe);
    }
}
