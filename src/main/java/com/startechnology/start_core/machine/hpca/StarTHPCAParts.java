package com.startechnology.start_core.machine.hpca;

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
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

import static com.gregtechceu.gtceu.common.data.machines.GTResearchMachines.OVERHEAT_TOOLTIPS;
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createHPCAPartModel;

public class StarTHPCAParts {
    private static MachineBuilder<MachineDefinition> registerHPCAPart(
        String name,
        String displayName,
        String texture,
        ResourceLocation damagedLocation,
        Function<IMachineBlockEntity, MetaMachine> machineConstructor
    ) {
        return StarTCore.START_REGISTRATE.machine(name, machineConstructor)
            .langValue(displayName)
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.HPCA_COMPONENT)
            .modelProperty(GTMachineModelProperties.IS_FORMED, false)
            .modelProperty(GTMachineModelProperties.IS_HPCA_PART_DAMAGED, false)
            .modelProperty(GTMachineModelProperties.IS_ACTIVE, false)
            .model(createHPCAPartModel(false,
                StarTCore.resourceLocation("block/overlay/hpca/" + texture),
                damagedLocation
            ));
    }

    public static final MachineDefinition HPCA_NANOFLUIDIC_HEAT_SINK_COMPONENT = registerHPCAPart(
        "hpca_nanofluidic_heat_sink_component",
        "HPCA Nanofluidic Heat Sink Component",
        "nanofluidic_heat_sink_component",
        GTCEu.id("block/overlay/machine/hpca/damaged"),
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
            Component.translatable("gtceu.machine.hpca.component_type.cooler_cooling", 3),
            Component.translatable("gtceu.part_sharing.disabled"))
        .register();

    public static final MachineDefinition HPCA_OPTIMIZED_COMPUTATION_COMPONENT = registerHPCAPart(
        "hpca_optimized_computation_component",
        "HPCA Optimized Computation Component",
        "optimized_computation",
        StarTCore.resourceLocation("block/overlay/hpca/damaged_optimized"),
        holder -> new StarTHPCAComputationPartMachine(
            holder,
            StarTGuiTextures.HPCA_ICON_OPTIMIZED_COMPUTATION_COMPONENT,
            StarTGuiTextures.HPCA_ICON_OPTIMIZED_DAMAGED_COMPUTATION_COMPONENT,
            GTValues.VA[GTValues.ZPM],
            GTValues.VA[GTValues.UHV],
            64,
            16
        )
    )
        .tooltips(
            Component.translatable("gtceu.machine.hpca.component_general.upkeep_eut", GTValues.VA[GTValues.ZPM]),
            Component.translatable("gtceu.machine.hpca.component_general.max_eut", GTValues.VA[GTValues.UHV]),
            Component.translatable("gtceu.machine.hpca.component_type.computation_cwut", 64),
            Component.translatable("gtceu.machine.hpca.component_type.computation_cooling", 16),
            Component.translatable("gtceu.part_sharing.disabled"))
        .tooltipBuilder(OVERHEAT_TOOLTIPS)
        .register();

    public static void init() {
    }
}

