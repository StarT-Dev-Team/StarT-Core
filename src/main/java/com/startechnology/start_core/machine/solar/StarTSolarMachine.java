package com.startechnology.start_core.machine.solar;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.startechnology.start_core.machine.solar.cell.StarTSolarCell;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StarTSolarMachine extends WorkableElectricMultiblockMachine {
    @Getter
    private final int tier;
    private final List<StarTSolarCell> panels;
    private boolean isWorking;

    public StarTSolarMachine(IMachineBlockEntity holder, int tier) {
        super(holder);

        this.tier = tier;
        this.panels = new ArrayList<>();
        this.isWorking = false;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        this.getParts().forEach(part -> {
            if (part instanceof StarTSolarCell) {
                panels.add((StarTSolarCell) part);
            }
        });
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        boolean isWorking = super.beforeWorking(recipe);

        if (isWorking) {
            this.isWorking = true;
        }

        return isWorking;
    }

    @Override
    public void afterWorking() {
        super.afterWorking();

        this.isWorking = false;

        panels.forEach(panel -> {
            if (panel instanceof StarTSolarCell) {
                panel.doLogic();
            }
        });
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof StarTSolarMachine starTSolarMachine)) {
            return RecipeModifier.nullWrongType(StarTSolarMachine.class, machine);
        }

        AtomicInteger euT = new AtomicInteger();
        AtomicInteger workingPanels = new AtomicInteger();

        starTSolarMachine.panels.forEach(starTSolarCell -> {
            if (starTSolarCell instanceof StarTSolarCell) {
                if (!starTSolarCell.getSolarCellBlockEntity().isBroken()) {
                    euT.addAndGet(GTValues.VHA[starTSolarCell.getSolarCellType().getTier()]);
                    workingPanels.incrementAndGet();
                }
            }
        });

        if (workingPanels.get() == 0) {
            return ModifierFunction.NULL;
        }

        return ModifierFunction.builder()
                .eutModifier(ContentModifier.addition(euT.get()))
                .build();
    }

    public boolean regressWhenWaiting() {
        return false;
    }

    public boolean canVoidRecipeOutputs(RecipeCapability<?> capability) {
        return capability != EURecipeCapability.CAP;
    }
}
