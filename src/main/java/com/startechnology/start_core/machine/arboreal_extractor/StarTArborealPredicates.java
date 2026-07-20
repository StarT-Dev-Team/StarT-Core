package com.startechnology.start_core.machine.arboreal_extractor;

import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.error.PatternStringError;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.startechnology.start_core.block.arboreal_extractor.ArborealBlocks;

import net.minecraft.network.chat.Component;

public class StarTArborealPredicates {

    public static String CONTEXT_KEY_TREE_TYPE = "treeType";

    public static TraceabilityPredicate leaves() {
        return new TraceabilityPredicate(blockWorldState -> {
            var blockState = blockWorldState.getBlockState();
            for (var tree : ArborealBlocks.TREES) {
                if (blockState.is(tree.getLeaves().get())) {
                    var currentTree = blockWorldState.getMatchContext().getOrPut(CONTEXT_KEY_TREE_TYPE, tree);
                    if (!currentTree.equals(tree)) {
                        blockWorldState.setError(
                                new PatternStringError("start_core.multiblock.pattern.error.arboreal_tree"));
                        return false;
                    }
                    return true;
                }
            }
            return false;
        }, () -> ArborealBlocks.TREES.stream()
                .map(tree -> BlockInfo.fromBlockState(tree.getLeaves().get().defaultBlockState()))
                .toArray(BlockInfo[]::new))
                .addTooltips(Component.translatable("start_core.multiblock.pattern.error.arboreal_tree"));
    }

    public static TraceabilityPredicate logs() {
        return new TraceabilityPredicate(blockWorldState -> {
            var blockState = blockWorldState.getBlockState();
            for (var tree : ArborealBlocks.TREES) {
                if (blockState.is(tree.getLog().get())) {
                    var currentTree = blockWorldState.getMatchContext().getOrPut(CONTEXT_KEY_TREE_TYPE, tree);
                    if (!currentTree.equals(tree)) {
                        blockWorldState.setError(
                                new PatternStringError("start_core.multiblock.pattern.error.arboreal_tree"));
                        return false;
                    }
                    return true;
                }
            }
            return false;
        }, () -> ArborealBlocks.TREES.stream()
                .map(tree -> BlockInfo.fromBlockState(tree.getLog().get().defaultBlockState()))
                .toArray(BlockInfo[]::new))
                .addTooltips(Component.translatable("start_core.multiblock.pattern.error.arboreal_tree"));
    }
}
