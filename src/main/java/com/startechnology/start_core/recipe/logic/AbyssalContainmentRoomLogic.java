package com.startechnology.start_core.recipe.logic;

import com.gregtechceu.gtceu.common.machine.trait.CleanroomLogic;
import com.startechnology.start_core.machine.abyssal_containment.StarTAbyssalContainmentMachine;

public class AbyssalContainmentRoomLogic extends CleanroomLogic {

    StarTAbyssalContainmentMachine abyssalContainmentMachine;

    public AbyssalContainmentRoomLogic(StarTAbyssalContainmentMachine machine) {
        super(machine);
        abyssalContainmentMachine = machine;
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
