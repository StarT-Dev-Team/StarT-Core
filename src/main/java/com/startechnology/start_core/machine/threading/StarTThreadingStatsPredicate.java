package com.startechnology.start_core.machine.threading;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.gregtechceu.gtceu.api.pattern.MultiblockState;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.lowdragmc.lowdraglib.utils.BlockInfo;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class StarTThreadingStatsPredicate {
    public static String THREADING_STATS_HEADER = "threading_stats_";

    public static class ThreadingStatsBlockTracker {
        public int general;
        public int speed;
        public int efficiency;
        public int parallels;
        public int threading;
        public int amount;
        public String name;

        public ThreadingStatsBlockTracker(String name, int general, int speed, int efficiency, int parallels,
                int threading) {
            this.general = general;
            this.name = THREADING_STATS_HEADER + name;
            this.speed = speed;
            this.efficiency = efficiency;
            this.parallels = parallels;
            this.threading = threading;
            amount = 0;
        }

        public void increment() {
            this.amount += 1;
        }

    }

    public static final Map<ThreadingStatsBlockTracker, Supplier<Block>> THREADING_STAT_BLOCKS = new HashMap<>();

    public static boolean traceThreadingStatBlocks(MultiblockState blockWorldState) {
        BlockState state = blockWorldState.getBlockState();
        for (Map.Entry<ThreadingStatsBlockTracker, Supplier<Block>> entry : THREADING_STAT_BLOCKS.entrySet()) {
            if (state.is(entry.getValue().get())) {
                ThreadingStatsBlockTracker stats = entry.getKey();

                ThreadingStatsBlockTracker currentStats = blockWorldState.getMatchContext().getOrDefault(stats.name,
                        new ThreadingStatsBlockTracker(stats.name, stats.general, stats.speed, stats.efficiency,
                                stats.parallels, stats.threading));
                
                currentStats.increment();
                blockWorldState.getMatchContext().set(stats.name, currentStats);
                return true;
            }
        }
        return false;
    }

    public static Predicate<MultiblockState> threadingStatBlocksPredicate = StarTThreadingStatsPredicate::traceThreadingStatBlocks;

    public static TraceabilityPredicate threadingStatBlocks() {
        return new TraceabilityPredicate(threadingStatBlocksPredicate, () -> THREADING_STAT_BLOCKS.entrySet().stream()
                .map(entry -> new BlockInfo(entry.getValue().get().defaultBlockState(), null))
                .toArray(BlockInfo[]::new));
    }
}
