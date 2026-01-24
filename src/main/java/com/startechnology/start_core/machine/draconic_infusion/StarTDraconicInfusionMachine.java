package com.startechnology.start_core.machine.draconic_infusion;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.AssemblyLineMachine;
import com.gregtechceu.gtceu.config.ConfigHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

public class StarTDraconicInfusionMachine extends WorkableElectricMultiblockMachine {

    /// Mapped item inventory looks something like this:
    /// 
    /// 
    ///         5           6
    ///     2         3         4
    ///         0           1
    /// 
    private static final List<Integer> RECIPE_INPUT_MAP = List.of(
        3,
        5,
        6,
        2,
        4,
        0,
        1
    );

    public StarTDraconicInfusionMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    public static Comparator<IMultiPart> partSorter(MultiblockControllerMachine mc) {
        // Sort first by going back and then going right.
        Comparator<IMultiPart> backSort = Comparator.comparing(p -> p.self().getPos(),
                    RelativeDirection.BACK.getSorter(mc.getFrontFacing(), mc.getUpwardsFacing(), mc.isFlipped()));
        
        Comparator<IMultiPart> leftSort = Comparator.comparing(p -> p.self().getPos(),
                    RelativeDirection.LEFT.getSorter(mc.getFrontFacing(), mc.getUpwardsFacing(), mc.isFlipped()));
        
        return backSort.thenComparing(leftSort);
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        var itemInputs = recipe.inputs.getOrDefault(ItemRecipeCapability.CAP, Collections.emptyList());
        if (itemInputs.isEmpty()) return true;

        int inputsSize = itemInputs.size();
        var itemHandlers = getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP);
        if (itemHandlers.size() < inputsSize) return false;

        var itemInventory = itemHandlers.stream()
                .filter(IRecipeHandler::shouldSearchContent)
                .map(container -> container.getContents().stream()
                        .filter(ItemStack.class::isInstance)
                        .map(ItemStack.class::cast)
                        .filter(s -> !s.isEmpty())
                        .findFirst())
                .limit(inputsSize)
                .map(o -> o.orElse(ItemStack.EMPTY))
                .toList();

        if (itemInventory.size() < inputsSize) return false;

        for (int i = 0; i < inputsSize; i++) {
            var itemStack = itemInventory.get(RECIPE_INPUT_MAP.get(i));
            Ingredient recipeStack = ItemRecipeCapability.CAP.of(itemInputs.get(i).content);
            if (!recipeStack.test(itemStack)) {
                return false;
            }
        }

        return super.beforeWorking(recipe);
    }

    @Override
    public void onStructureFormed() {
        getDefinition().setPartSorter(StarTDraconicInfusionMachine::partSorter);
        super.onStructureFormed();
    }
}
