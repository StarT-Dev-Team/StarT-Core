package com.startechnology.start_core.machine.solar.cell;

import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.startechnology.start_core.block.solar.StarTSolarCellBlocks;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

import java.util.Comparator;

public class StarTSolarCellPredicates {
    public static TraceabilityPredicate solarCells() {
        return new TraceabilityPredicate(blockWorldState -> {
            var blockState = blockWorldState.getBlockState();

            for (var solarCell : StarTSolarCellBlocks.SOLAR_CELLS.entrySet()) {
                if (blockState.is(solarCell.getValue().get())) {
                    blockWorldState.getMatchContext()
                        .getOrCreate("cellPositions", LongOpenHashSet::new)
                        .add(blockWorldState.getPos().asLong());

                    return true;
                }
            }

            return false;
        }, () -> StarTSolarCellBlocks.SOLAR_CELLS.entrySet().stream()
            .sorted(Comparator.comparingInt(block -> block.getKey().getTier()))
            .map(block -> BlockInfo.fromBlockState(block.getValue().get().defaultBlockState()))
            .toArray(BlockInfo[]::new));
    }
}
