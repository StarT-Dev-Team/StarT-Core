package com.startechnology.start_core.machine.threading;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.StarTPartAbility;

import net.minecraft.network.chat.Component;

public class StarTThreadingControllerMachines {

    public static final MachineDefinition THREADING_CONTROLLER = createThreadingController(
            "threading_controller")
            .tier(GTValues.UIV)
            .workableTieredHullRenderer(StarTCore.resourceLocation("block/threading/threading_controller"))
            .register();

    public static MachineBuilder<MachineDefinition> createThreadingController(String name) {
        return StarTCore.START_REGISTRATE.machine(
                name, StarTThreadingControllerPartMachine::new)
                .rotationState(RotationState.ALL)
                .abilities(StarTPartAbility.THREADING_CONTROLLER);
    }

    public static void init() {
    }

}
