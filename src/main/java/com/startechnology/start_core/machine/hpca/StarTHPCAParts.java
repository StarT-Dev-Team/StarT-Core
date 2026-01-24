package com.startechnology.start_core.machine.hpca;

import java.util.function.Function;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.gui.StarTGuiTextures;

import net.minecraft.network.chat.Component;

import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createHPCAPartModel;

public class StarTHPCAParts {
    private static MachineBuilder<MachineDefinition> registerHPCAPart(
        String name,
        String texture,
        Function<IMachineBlockEntity, MetaMachine> machineConstructor
    ) {
        return StarTCore.START_REGISTRATE.machine(name, machineConstructor)
                .abilities(PartAbility.HPCA_COMPONENT)
                .rotationState(RotationState.ALL)
                .modelProperty(GTMachineModelProperties.IS_FORMED, false)
                .modelProperty(GTMachineModelProperties.IS_HPCA_PART_DAMAGED, false)
                .modelProperty(GTMachineModelProperties.IS_ACTIVE, false)
                .model(createHPCAPartModel(false,
                        GTCEu.id("block/overlay/machine/hpca/" + texture),
                        GTCEu.id("block/overlay/machine/hpca/damaged"))
                );
    }

    public static final MachineDefinition HPCA_NANOFLUIDIC_HEAT_SINK_COMPONENT = registerHPCAPart(
        "hpca_nanofluidic_heat_sink_component", 
        "nanofluidic_heat_sink_component",
        holder -> new StarTHPCAPassiveCoolingPart(
            holder,
            StarTGuiTextures.NANOFLUIDIC_HEAT_SINK_COMPONENT,
            GTValues.VA[GTValues.ZPM],
            3
        )
    )
    .tooltips(
            Component.translatable("gtceu.machine.hpca.component_general.upkeep_eut", GTValues.VA[GTValues.ZPM]),
            Component.translatable("gtceu.machine.hpca.component_type.cooler_passive"),
            Component.translatable("gtceu.machine.hpca.component_type.cooler_cooling", 3))
    .register();

    public static void init() {}
}

