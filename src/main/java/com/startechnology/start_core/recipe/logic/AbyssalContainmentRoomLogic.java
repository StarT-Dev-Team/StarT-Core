package com.startechnology.start_core.recipe.logic;

import com.gregtechceu.gtceu.common.machine.trait.CleanroomLogic;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.machine.abyssal_containment.StarTAbyssalContainmentMachine;

public class AbyssalContainmentRoomLogic extends CleanroomLogic {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            AbyssalContainmentRoomLogic.class,
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
