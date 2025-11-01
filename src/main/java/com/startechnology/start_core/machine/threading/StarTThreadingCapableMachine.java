package com.startechnology.start_core.machine.threading;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;

public class StarTThreadingCapableMachine extends WorkableElectricMultiblockMachine {

    public StarTThreadingCapableMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        this.getParts().forEach(part -> {
            if (part instanceof StarTThreadingControllerPartMachine threadingController) {
                threadingController.updateControllerStats();
            }
        });
    }
    
}
