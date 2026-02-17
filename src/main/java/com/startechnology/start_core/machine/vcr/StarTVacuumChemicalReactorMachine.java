package com.startechnology.start_core.machine.vcr;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.startechnology.start_core.api.reflector.FusionReflectorType;
import com.startechnology.start_core.api.vacuumpump.IVacuumPumpType;
import com.startechnology.start_core.block.VacuumPumpBlock;
import com.startechnology.start_core.machine.redstone.StarTRedstoneInterfacePartMachine;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;


public class StarTVacuumChemicalReactorMachine extends WorkableElectricMultiblockMachine {

    // Defining Main Variables
    @Persisted
    protected Integer pressure;
    protected Integer partialvacuum;
    protected Integer perfectvacuum;

    @Getter
    private IVacuumPumpType pumpType = VacuumPumpBlock.VacuumPumpType.ZPM;

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

            this.pumpType = getMultiblockState().getMatchContext().get("VacuumPumpType");

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




