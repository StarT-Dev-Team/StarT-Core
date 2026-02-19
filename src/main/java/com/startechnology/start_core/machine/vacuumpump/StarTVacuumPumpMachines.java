package com.startechnology.start_core.machine.vacuumpump;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.machine.StarTPartAbility;
import net.minecraft.network.chat.Component;

import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createOverlayTieredHullMachineModel;

public class StarTVacuumPumpMachines {

    public static final MachineDefinition[] VACUUM_PUMP = StarTMachineUtils.registerTieredMachines("vacuum_pump",
            VacuumPumpPartMachine::new,
            (tier, builder) -> builder
                    .langValue(GTValues.VNF[tier] + "§r Vacuum Pump")
                    .rotationState(RotationState.ALL)
                    .modelProperty(GTMachineModelProperties.IS_FORMED, false)
                    .abilities(StarTPartAbility.VACUUM_PUMP)
                    .overlayTieredHullModel(StarTCore.resourceLocation("block/vacuum_pump"))
                    .tooltips(
                            Component.translatable("start_core.machine.vacuum_pump.tooltip_cap",
                                    VacuumPumpPartMachine.formatVacuumPumpCap(VacuumPumpPartMachine.getVacuumCap(tier))),
                            Component.translatable("start_core.machine.vacuum_pump.tooltip_rate",
                                    VacuumPumpPartMachine.formatVacuumPumpRate(VacuumPumpPartMachine.getVacuumRate(tier)))
                    )
                    .register(),
            GTValues.ZPM, GTValues.UV, GTValues.UHV, GTValues.UEV, GTValues.UIV
    );

    public static void init() {
    }

}
