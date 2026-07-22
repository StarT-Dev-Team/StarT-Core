package com.startechnology.start_core.machine.arboreal_extractor;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.multiblock.primitive.PrimitiveFancyUIWorkableMachine;
import com.startechnology.start_core.block.arboreal_extractor.TreeType;
import lombok.Getter;

public class ArborealExtractorMachine extends PrimitiveFancyUIWorkableMachine {

    @Getter
    private TreeType treeType;

    public ArborealExtractorMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        var treeTypeCandidate = StarTArborealPredicates.getTreeTypeCandidates(getMultiblockState());
        if (treeTypeCandidate == null || treeTypeCandidate.size() != 1) {
            onStructureInvalid();
            return;
        }
        this.treeType = treeTypeCandidate.iterator().next().getTreeType();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        this.treeType = null;
    }
}
