package com.startechnology.start_core.machine.arboreal_extractor;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.multiblock.primitive.PrimitiveFancyUIWorkableMachine;
import com.startechnology.start_core.block.arboreal_extractor.TreeDefinition;
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
        var leavesType = getMultiblockState().getMatchContext().get(StarTArborealPredicates.CONTEXT_KEY_TREE_TYPE);
        if (!(leavesType instanceof TreeDefinition treeDefinition)) {
            onStructureInvalid();
            return;
        }

        this.treeType = treeDefinition.getTreeType();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        this.treeType = null;
    }
}
