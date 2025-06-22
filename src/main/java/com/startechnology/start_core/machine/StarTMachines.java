package com.startechnology.start_core.machine;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.client.renderer.machine.MaintenanceHatchPartRenderer;
import com.gregtechceu.gtceu.common.machine.multiblock.part.CleaningMaintenanceHatchPartMachine;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.bacteria.StarTBacteriaMachines;
import com.startechnology.start_core.machine.converter.StarTConverterMachine;
import com.startechnology.start_core.machine.dreamlink.StarTDreamLinkHatches;
import com.startechnology.start_core.machine.dreamlink.StarTDreamLinkTransmissionTowers;
import com.startechnology.start_core.machine.drum.StarTDrumMachines;
import com.startechnology.start_core.machine.fusion.StarTFusionMachines;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachines;
import com.startechnology.start_core.machine.hpca.StarTHPCAParts;
import com.startechnology.start_core.machine.maintenance.StarTMaintenanceMachines;
import com.startechnology.start_core.machine.parallel.StarTParallelHatches;
import com.startechnology.start_core.machine.redstone.StarTRedstoneInterfaces;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class StarTMachines {

    public static void init() {
        StarTHPCAParts.init();
        StarTBacteriaMachines.init();
        StarTFusionMachines.init();
        StarTConverterMachine.init();
        StarTParallelHatches.init();
        StarTDrumMachines.init();
        StarTDreamLinkHatches.init();
        StarTDreamLinkTransmissionTowers.init();
        StarTHellForgeMachines.init();
        StarTRedstoneInterfaces.init();
        StarTMaintenanceMachines.init();
    }
}
