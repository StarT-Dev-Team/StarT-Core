package com.startechnology.start_core.machine.maintenance;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.common.machine.multiblock.part.CleaningMaintenanceHatchPartMachine;
import com.startechnology.start_core.StarTCore;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createMaintenanceModel;

public class StarTMaintenanceMachines {
    public static final MachineDefinition STERILE_CLEANING_MAINTENANCE_HATCH = StarTCore.START_REGISTRATE
        .machine("sterile_cleaning_maintenance_hatch",
                holder -> new CleaningMaintenanceHatchPartMachine(holder, CleanroomType.STERILE_CLEANROOM))
        .rotationState(RotationState.ALL)
        .abilities(PartAbility.MAINTENANCE)
        .tooltips(Component.translatable("gtceu.universal.disabled"),
                Component.translatable("start_core.machine.sterile_hatch.tooltip"),
                Component.translatable("gtceu.machine.maintenance_hatch_cleanroom_auto.tooltip.1"))
        .tooltipBuilder((stack, tooltips) -> {
            tooltips.add(Component.literal("  ").append(Component
                    .translatable(CleanroomType.STERILE_CLEANROOM.getTranslationKey()).withStyle(ChatFormatting.GOLD)));
        })
        .modelProperty(GTMachineModelProperties.IS_FORMED, false)
        .modelProperty(GTMachineModelProperties.IS_TAPED, false)
        .model(createMaintenanceModel(StarTCore.resourceLocation("block/maintenance_sterile")))
        .tier(GTValues.HV)
        .register();

    public static void init() {}
}
