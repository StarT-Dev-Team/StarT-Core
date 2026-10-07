package com.startechnology.start_core.data.machines.multiblock.resource_production;

import com.startechnology.start_core.data.machines.multiblock.resource_production.plants.StarTGCropMachines;
import com.startechnology.start_core.data.machines.multiblock.resource_production.plants.StarTGenomeMachines;

public class StarTResourceProductionMultiblocks {

    public static void init() {
        StarTAbyssalharvesterMachines.init();
        StarTBacteriaMachines.init();
        StarTDrillingRigs.init();
        StarTVoidMesh.init();
        StarTGCropMachines.init();
        StarTGenomeMachines.init();
    }
}
