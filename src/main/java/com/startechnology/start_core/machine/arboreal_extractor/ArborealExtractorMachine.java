package com.startechnology.start_core.machine.arboreal_extractor;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.startechnology.start_core.block.arboreal_extractor.LeavesType;
import com.startechnology.start_core.block.arboreal_extractor.LogType;

import lombok.Getter;

public class ArborealExtractorMachine extends WorkableMultiblockMachine {

    @Getter
    private LeavesType leavesType;
    @Getter
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

    public boolean typesMatch() {
        return this.leavesType.getName().equals(this.logType.getName());
    }

    public @NotNull String getTreeType() {
        return this.logType.getName();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        this.leavesType = null;
        this.logType = null;
    }
}
