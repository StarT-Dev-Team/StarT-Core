package com.startechnology.start_core.machine.lapidaries;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.MethodsReturnNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LapidaryGeneratorMachine extends WorkableElectricMultiblockMachine {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(LapidaryGeneratorMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @DescSynced
    @Getter
    @Setter
    private int gemParallels = 1;

    @Persisted
    @DescSynced
    @Getter
    @Setter
    private int maxGemParallels = 8;

    public LapidaryGeneratorMachine(IMachineBlockEntity holder, int parallelLimit) {
        super(holder);
        this.maxGemParallels = parallelLimit;
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof LapidaryGeneratorMachine lapidaryGenerator)) {
            return RecipeModifier.nullWrongType(LapidaryGeneratorMachine.class, machine);
        }
        return ModifierFunction.builder().parallels(lapidaryGenerator.gemParallels).build();
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}
