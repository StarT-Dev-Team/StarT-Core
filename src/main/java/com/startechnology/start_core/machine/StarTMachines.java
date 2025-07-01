package com.startechnology.start_core.machine;
import com.startechnology.start_core.machine.abyssal_harvester.StarTAbyssalharvesterMachines;
import com.startechnology.start_core.machine.bacteria.StarTBacteriaMachines;
import com.startechnology.start_core.machine.converter.StarTConverterMachine;
import com.startechnology.start_core.machine.dreamlink.StarTDreamLinkHatches;
import com.startechnology.start_core.machine.dreamlink.StarTDreamLinkTransmissionTowers;
import com.startechnology.start_core.machine.drum.StarTDrumMachines;
import com.startechnology.start_core.machine.fusion.StarTFusionMachines;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachines;
import com.startechnology.start_core.machine.hpca.StarTHPCAParts;
import com.startechnology.start_core.machine.parallel.StarTParallelHatches;
import com.startechnology.start_core.machine.redstone.StarTRedstoneInterfaces;

import src.main.java.com.startechnology.start_core.machine.furnaces.StarTFurnaceMachines;

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
        StarTAbyssalharvesterMachines.init();
        StarTFurnaceMachines.init();
    }
}
