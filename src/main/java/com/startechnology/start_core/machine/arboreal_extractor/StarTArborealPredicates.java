package com.startechnology.start_core.machine.arboreal_extractor;

import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.startechnology.start_core.block.arboreal_extractor.ArborealBlocks;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

public class StarTArborealPredicates {

    public static TraceabilityPredicate leaves() {
        return new TraceabilityPredicate(blockWorldState -> {
            var blockState = blockWorldState.getBlockState();
            for (var leaves : ArborealBlocks.LEAVES.entrySet()) {
                if (blockState.is(leaves.getValue().get())) {
                    blockWorldState.getMatchContext()
                            .getOrCreate("leavesPositions", LongOpenHashSet::new)
                            .add(blockWorldState.getPos().asLong());
                    return true;
                }
            }
            return false;
        }, () -> ArborealBlocks.LEAVES.entrySet().stream()
                .map(block -> BlockInfo.fromBlockState(block.getValue().get().defaultBlockState()))
                .toArray(BlockInfo[]::new));
    }

    public static TraceabilityPredicate logs() {
        return new TraceabilityPredicate(blockWorldState -> {
            var blockState = blockWorldState.getBlockState();
            for (var log : ArborealBlocks.LOGS.entrySet()) {
                if (blockState.is(log.getValue().get())) {
                    blockWorldState.getMatchContext()
                            .getOrCreate("logPositions", LongOpenHashSet::new)
                            .add(blockWorldState.getPos().asLong());
                    return true;
                }
            }
            return false;
        }, () -> ArborealBlocks.LOGS.entrySet().stream()
                .map(block -> BlockInfo.fromBlockState(block.getValue().get().defaultBlockState()))
                .toArray(BlockInfo[]::new));
    }
}
