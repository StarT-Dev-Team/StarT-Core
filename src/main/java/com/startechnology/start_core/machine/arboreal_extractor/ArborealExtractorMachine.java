package com.startechnology.start_core.machine.arboreal_extractor;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.startechnology.start_core.block.arboreal_extractor.LeavesType;
import com.startechnology.start_core.block.arboreal_extractor.LogType;

public class ArborealExtractorMachine extends WorkableElectricMultiblockMachine {

    private LeavesType leavesType;
    private LogType logType;

    public ArborealExtractorMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        var leavesType = getMultiblockState().getMatchContext().get("leavesPositions");
        if (leavesType instanceof LeavesType leaves) {
            this.leavesType = leaves;
        }
        var logType = getMultiblockState().getMatchContext().get("logPositions");
        if (logType instanceof LogType log) {
            this.logType = log;
        }
    }
    
}
