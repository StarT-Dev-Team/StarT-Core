package com.startechnology.start_core.data.machines;

import com.startechnology.start_core.block.threading.StarTThreadingStatBlocks;
import com.startechnology.start_core.data.machines.multiblock.end.StarTEndMultiblocks;
import com.startechnology.start_core.data.machines.multiblock.intermediate.StarTIntermediateMultiblocks;
import com.startechnology.start_core.data.machines.multiblock.nether.StarTNetherMultiblocks;
import com.startechnology.start_core.data.machines.multiblock.power.StarTPowerMultiblocks;
import com.startechnology.start_core.data.machines.multiblock.primitive.PrimitiveMultiblocks;
import com.startechnology.start_core.data.machines.multiblock.resource_production.StarTResourceProductionMultiblocks;
import com.startechnology.start_core.data.machines.multiblock.riftic.StarTRifticMultiblocks;
import com.startechnology.start_core.data.machines.multiblock.utility.StarTUtilityMultiblocks;
import com.startechnology.start_core.data.machines.part.StarTParts;
import com.startechnology.start_core.data.machines.single.StarTSingleblocks;

public class StarTMachines {

    public static void init() {
        StarTThreadingStatBlocks.init();

        StarTParts.init();
        StarTSingleblocks.init();

        PrimitiveMultiblocks.init();
        StarTIntermediateMultiblocks.init();
        StarTResourceProductionMultiblocks.init();
        StarTPowerMultiblocks.init();
        StarTNetherMultiblocks.init();
        StarTEndMultiblocks.init();
        StarTRifticMultiblocks.init();
        StarTUtilityMultiblocks.init();
    }
}
