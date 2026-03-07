package com.startechnology.start_core.machine.fusion;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.error.PatternStringError;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.startechnology.start_core.api.StarTAPI;
import net.minecraft.network.chat.Component;

import java.util.Comparator;

public class StarTReflectorPredicates {

    public static TraceabilityPredicate fusionReflectors() {
        return new TraceabilityPredicate(blockWorldState -> {
            var blockState = blockWorldState.getBlockState();
            for (var entry : StarTAPI.FUSION_REFLECTORS.entrySet()) {
                if (blockState.is(entry.getValue().get())) {
                    var stats = entry.getKey();
                    var currentReflector = blockWorldState.getMatchContext().getOrPut("ReflectorType", stats);
                    if (!currentReflector.equals(stats)) {
                        blockWorldState.setError(new PatternStringError("start_core.multiblock.pattern.error.reflector"));
                        return false;
                    }
                    return true;
                }
            }
            return false;
        }, () -> StarTAPI.FUSION_REFLECTORS.entrySet().stream()
                .sorted(Comparator.comparingInt(value -> value.getKey().getTier()))
                .map(reflector -> BlockInfo.fromBlockState(reflector.getValue().get().defaultBlockState()))
                .toArray(BlockInfo[]::new))
                .addTooltips(Component.translatable("start_core.multiblock.pattern.error.reflector"));
    }
}
