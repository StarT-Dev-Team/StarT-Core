package com.startechnology.start_core.machine.vacuumpump;

import com.gregtechceu.gtceu.api.block.IMachineBlock;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.error.PatternStringError;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.startechnology.start_core.machine.StarTPartAbility;
import net.minecraft.network.chat.Component;

import java.util.Comparator;

public class StarTVacuumPumpPredicates {

    public static TraceabilityPredicate vacuumPumps() {
        return new TraceabilityPredicate(blockWorldState -> {
            var blockState = blockWorldState.getBlockState();
            for (var block : StarTPartAbility.VACUUM_PUMP.getAllBlocks()) {
                if (blockState.is(block)) {
                    var currentPump = blockWorldState.getMatchContext().getOrPut("VacuumPumpBlock", block);
                    if (currentPump != block) {
                        blockWorldState.setError(new PatternStringError("start_core.multiblock.pattern.error.vacuumpump"));
                        return false;
                    }
                    return true;
                }
            }
            return false;
        }, () -> StarTPartAbility.VACUUM_PUMP.getAllBlocks().stream()
                .sorted(Comparator.comparingDouble(block -> block instanceof IMachineBlock machineBlock ? machineBlock.getDefinition().getTier() : 0))
                .map(pump -> BlockInfo.fromBlockState(pump.defaultBlockState()))
                .toArray(BlockInfo[]::new))
                .addTooltips(Component.translatable("start_core.multiblock.pattern.error.vacuumpump"));
    }
}
