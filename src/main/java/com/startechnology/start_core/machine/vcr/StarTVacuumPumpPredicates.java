package com.startechnology.start_core.machine.vcr;

import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.error.PatternStringError;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.startechnology.start_core.api.StarTAPI;
import net.minecraft.network.chat.Component;

import java.util.Comparator;

public class StarTVacuumPumpPredicates {

    public static TraceabilityPredicate vacuumPumps() {
        return new TraceabilityPredicate(blockWorldState -> {
            var blockState = blockWorldState.getBlockState();
            for (var entry : StarTAPI.VACUUM_PUMPS.entrySet()) {
                if (blockState.is(entry.getValue().get())) {
                    var stats = entry.getKey();
                    var currentPump = blockWorldState.getMatchContext().getOrPut("VacuumPumpType", stats);
                    if (!currentPump.equals(stats)) {
                        blockWorldState.setError(new PatternStringError("start_core.multiblock.pattern.error.vacuumpump"));
                        return false;
                    }
                    return true;
                }
            }
            return false;
        }, () -> StarTAPI.VACUUM_PUMPS.entrySet().stream()
                .sorted(Comparator.comparingDouble(value -> value.getKey().getCap()))
                .map(pump -> BlockInfo.fromBlockState(pump.getValue().get().defaultBlockState()))
                .toArray(BlockInfo[]::new))
                .addTooltips(Component.translatable("start_core.multiblock.pattern.error.vacuumpump"));
    }
}
