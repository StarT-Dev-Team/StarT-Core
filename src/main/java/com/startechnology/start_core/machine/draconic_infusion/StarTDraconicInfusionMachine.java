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

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        var recipeInputs = recipe.inputs.get(ItemRecipeCapability.CAP);
        var itemInputInventory = Objects
                .requireNonNullElseGet(getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP),
                        Collections::<IRecipeHandler<?>>emptyList)
                .stream()
                .filter(handler -> !handler.isProxy())
                .map(container -> container.getContents().stream().filter(ItemStack.class::isInstance)
                        .map(ItemStack.class::cast).toList())
                .filter(container -> !container.isEmpty())
                .toList();
        

        if (itemInputInventory.size() < recipeInputs.size()) return false;

        for (int i = 0; i < recipeInputs.size(); i++) {
            var itemStack = itemInputInventory.get(RECIPE_INPUT_MAP.get(i)).get(0);
            Ingredient recipeStack = ItemRecipeCapability.CAP.of(recipeInputs.get(i).content);
            if (!recipeStack.test(itemStack)) {
                return false;
            }
        }

        return super.beforeWorking(recipe);
    }

    @Override
    public void onStructureFormed() {
        getDefinition()
            .setPartSorter(
                // Sort first by going back and then going right.
                Comparator.comparing(
                    (IMultiPart it) -> multiblockPartSorter(RelativeDirection.BACK).apply(it.self().getPos())
                ).thenComparing(
                    it -> multiblockPartSorter(RelativeDirection.LEFT).apply(it.self().getPos())
                ));
        super.onStructureFormed();
    }

    private Function<BlockPos, Integer> multiblockPartSorter(RelativeDirection direction) {
        return direction.getSorter(getFrontFacing(), getUpwardsFacing(), isFlipped());
    }
}
