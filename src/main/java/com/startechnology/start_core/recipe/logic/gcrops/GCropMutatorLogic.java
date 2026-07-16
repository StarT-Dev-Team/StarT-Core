package com.startechnology.start_core.recipe.logic.gcrops;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.api.gcrop.*;
import com.startechnology.start_core.item.components.StarTGCropBehaviour;
import com.startechnology.start_core.recipe.StarTRecipeTypes;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;
import java.util.stream.Collectors;

import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.startechnology.start_core.item.StarTGCropItems.GCROP_MALFORMED;

public class GCropMutatorLogic implements ICustomRecipeLogic {

    public GCropMutatorLogic() {}

    @Override
    public GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        Map<Boolean, List<NotifiableItemStackHandler>> itemHandlers = Objects
                .requireNonNullElseGet(holder.getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP),
                        Collections::emptyList)
                .stream()
                .filter(NotifiableItemStackHandler.class::isInstance)
                .map(NotifiableItemStackHandler.class::cast)
                .filter(i -> i.getSlots() >= 1)
                .collect(Collectors.groupingBy(NotifiableRecipeHandlerTrait::isDistinct));

        Map<Boolean, List<NotifiableFluidTank>> fluidHandlers = Objects
                .requireNonNullElseGet(holder.getCapabilitiesFlat(IO.IN, FluidRecipeCapability.CAP),
                        Collections::emptyList)
                .stream()
                .filter(NotifiableFluidTank.class::isInstance)
                .map(NotifiableFluidTank.class::cast)
                .filter(i -> i.getTanks() >= 1)
                .collect(Collectors.groupingBy(NotifiableRecipeHandlerTrait::isDistinct));

        if (itemHandlers.isEmpty() || fluidHandlers.isEmpty()) return null;

        // Distinct first, reset our stacks for every inventory
        for (var itemHandler : itemHandlers.getOrDefault(true, Collections.emptyList())) {
            for (var fluidHandler : fluidHandlers.getOrDefault(true, Collections.emptyList())) {
                GTRecipe recipe = createGCropRecipe(itemHandler, fluidHandler);
                if (recipe != null) return recipe;
            }
            for (var fluidHandler : fluidHandlers.getOrDefault(false, Collections.emptyList())) {
                GTRecipe recipe = createGCropRecipe(itemHandler, fluidHandler);
                if (recipe != null) return recipe;
            }
        }

        // Non-distinct, return as soon as we find valid items
        for (var itemHandler : itemHandlers.getOrDefault(false, Collections.emptyList())) {
            for (var fluidHandler : fluidHandlers.getOrDefault(true, Collections.emptyList())) {
                GTRecipe recipe = createGCropRecipe(itemHandler, fluidHandler);
                if (recipe != null) return recipe;
            }
            for (var fluidHandler : fluidHandlers.getOrDefault(false, Collections.emptyList())) {
                GTRecipe recipe = createGCropRecipe(itemHandler, fluidHandler);
                if (recipe != null) return recipe;
            }
        }

        return null;
    }

    public static boolean hasItemMatch(ItemStack item, List<ItemStack> itemList) {
        for (ItemStack newItem : itemList) {
            if (item.copyWithCount(1).equals(newItem)) return true;
        }
        return false;
    }

    public static boolean hasFluidMatch(FluidStack fluid, List<FluidStack> fluidList) {
        for (FluidStack newFluid : fluidList) {
            if (fluid.isFluidEqual(newFluid)) return true;
        }
        return false;
    }

    public static GTRecipe createGCropRecipe(NotifiableItemStackHandler itemHandler,
                                             NotifiableFluidTank fluidHandler) {
        ItemStack foundGCrop = ItemStack.EMPTY;

        final List<ItemStack> validMutationItemList = List.of(
                ChemicalHelper.get(dust, Thorium),
                ChemicalHelper.get(dust, Uranium238));

        final List<FluidStack> validMutationFluidList = List.of(
                GTMaterials.Radon.getFluid(1000),
                GTMaterials.Naquadria.getFluid(1000));

        List<ItemStack> validMutationItems = new ArrayList<>();
        List<FluidStack> validMutationFluids = new ArrayList<>();

        for (int i = 0; i < itemHandler.getSlots(); ++i) {
            ItemStack itemInSlot = itemHandler.getStackInSlot(i);
            if (!itemInSlot.isEmpty()) {
                if (StarTGCropBehaviour.getGCropBehaviour(itemInSlot) != null) {
                    foundGCrop = itemInSlot;
                } else if (hasItemMatch(itemInSlot, validMutationItemList)) {
                    validMutationItems.add(itemInSlot);
                }
            }
        }

        for (int i = 0; i < fluidHandler.getTanks(); ++i) {
            FluidStack fluidInSlot = fluidHandler.getFluidInTank(i);
            if (!fluidInSlot.isEmpty()) {
                if (hasFluidMatch(fluidInSlot, validMutationFluidList)) {
                    validMutationFluids.add(fluidInSlot);
                }
            }
        }

        if (foundGCrop.isEmpty() || (validMutationItems.isEmpty() && validMutationFluids.isEmpty())) return null;

        StarTGCropPlant existingStats = StarTGCropManager.gcropGenomeFromTag(foundGCrop);

        if (existingStats == null) return null;

        List<StarTGCropGene> existingResourceGenome = existingStats.getResourceGenome();
        List<StarTGCropGene> existingProductionGenome = existingStats.getProductionGenome();
        List<StarTGCropGene> existingAuxiliaryGenome = existingStats.getAuxiliaryGenome();

        ItemStack newGCrop = foundGCrop.copyWithCount(1);

        if (hasFluidMatch(GTMaterials.Radon.getFluid(1000), validMutationFluids)) {
            StarTGCropPlant newGenome = new StarTGCropPlant(existingResourceGenome, existingProductionGenome,
                    existingAuxiliaryGenome);

            StarTGCropManager.writeGCRopGenomeToItem(newGCrop.getOrCreateTag(), newGenome);

            return StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                    .recipeBuilder("runic_mutator_pathway")
                    .inputItems(foundGCrop.copyWithCount(1))
                    .inputFluids(GTMaterials.Radon.getFluid(1000))
                    .outputItems(newGCrop.copyWithCount(1))
                    .duration(400)
                    .EUtV(GTValues.MV)
                    .buildRawRecipe();
        }

        return null;
    }

    @Override
    public void buildRepresentativeRecipes() {
        ItemStack gCropInput = new ItemStack(GCROP_MALFORMED.get());
        StarTCustomTooltipsManager.writeCustomTooltipsToItem(gCropInput.getOrCreateTag(),
                "behaviour.start_core.bacteria.input");

        ItemStack bacteriaAffinityMutationOutput = new ItemStack(GCROP_MALFORMED.get());
        StarTCustomTooltipsManager.writeCustomTooltipsToItem(
                bacteriaAffinityMutationOutput.getOrCreateTag(),
                "behaviour.start_core.bacteria.mutator_affinity_output");

        GTRecipe affinityRecipe = StarTRecipeTypes.GCROP_MUTATOR_RECIPES
                .recipeBuilder("gcrop_copying")
                .inputItems(gCropInput.copyWithCount(1))
                .inputFluids(GTMaterials.Radon.getFluid(1000))
                .outputItems(bacteriaAffinityMutationOutput)
                .duration(400)
                .EUtV(GTValues.MV)
                .buildRawRecipe();

        // for EMI to detect it's a synthetic recipe (not ever in JSON)
        affinityRecipe.setId(affinityRecipe.getId().withPrefix("/"));
        StarTRecipeTypes.GCROP_MUTATOR_RECIPES.addToMainCategory(affinityRecipe);
    }
}
