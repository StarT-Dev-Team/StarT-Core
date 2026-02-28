package com.startechnology.start_core.machine.solar.cell;

import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.startechnology.start_core.block.solar.StarTSolarCellBlocks;
import com.tterrag.registrate.util.entry.RegistryEntry;

import java.util.Comparator;

public class StarTSolarCellPredicates {
    public static TraceabilityPredicate solarCells() {
        return new TraceabilityPredicate(blockWorldState -> {
            var blockState = blockWorldState.getBlockState();

            return StarTSolarCellBlocks.solarCells.stream()
                    .map(RegistryEntry::get)
                    .equals(blockState);
        }, () -> StarTSolarCellBlocks.solarCells.stream()
                .sorted(Comparator.comparingInt(block -> block.get().getSolarCellType().getTier()))
                .map(block -> BlockInfo.fromBlock(block.get()))
                .toArray(BlockInfo[]::new));
    }
}
