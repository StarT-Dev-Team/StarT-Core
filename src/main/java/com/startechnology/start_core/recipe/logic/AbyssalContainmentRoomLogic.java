package com.startechnology.start_core.recipe.logic;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.IWorkable;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMaintenanceMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.common.capability.EnvironmentalHazardSavedData;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.CleanroomMachine;
import com.gregtechceu.gtceu.common.machine.trait.CleanroomLogic;
import com.gregtechceu.gtceu.config.ConfigHolder;

import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.machine.abyssal_containment.StarTAbyssalContainmentMachine;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

public class AbyssalContainmentRoomLogic extends CleanroomLogic {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(AbyssalContainmentRoomLogic.class,
            CleanroomLogic.MANAGED_FIELD_HOLDER);

    StarTAbyssalContainmentMachine abyssalContainmentMachine;

    public AbyssalContainmentRoomLogic(StarTAbyssalContainmentMachine machine) {
        super(machine);
        abyssalContainmentMachine = machine;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public void serverTick() {
        super.serverTick();

        // Ensure is supplied fluids.
        if (!this.abyssalContainmentMachine.isFluidsSupplied()) {
            this.interruptRecipe();
            this.setProgress(0);
            adjustCleanAmount(true);
            setStatus(Status.WAITING);
        }
    }
}
