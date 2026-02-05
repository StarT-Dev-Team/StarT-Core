package com.startechnology.start_core.machine.vcr;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.startechnology.start_core.machine.redstone.StarTRedstoneInterfacePartMachine;
import com.startechnology.start_core.machine.vcr.VacuumPumpTier;

import java.util.ArrayList;


public class StarTVacuumChemicalReactorMachine extends WorkableElectricMultiblockMachine {

    // Defining Main Variables
    @Persisted
    protected Integer pressure;
    private boolean isWorking;
    private boolean startPressureLoss;

    public ArrayList<StarTRedstoneInterfacePartMachine> redstoneOutputHatches;


    public StarTVacuumChemicalReactorMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        this.pressure = 0;
        this.startPressureLoss = false;
        this.isWorking = false;
        this.redstoneOutputHatches = new ArrayList<>();
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        this.isWorking = false;
        this.startPressureLoss = true;

        this.getParts()
                .stream()
                .filter(StarTRedstoneInterfacePartMachine.class::isInstance)
                .forEach(part -> {
                    this.redstoneOutputHatches.add((StarTRedstoneInterfacePartMachine)part);

                });


    }

    public void onStructureInvalid() {
        super.onStructureInvalid();
        this.isWorking = false;
    }




}



