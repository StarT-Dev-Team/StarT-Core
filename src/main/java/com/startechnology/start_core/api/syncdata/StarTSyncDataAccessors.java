package com.startechnology.start_core.api.syncdata;

import com.lowdragmc.lowdraglib2.compat.network.ByteBufCodecs;
import com.lowdragmc.lowdraglib2.syncdata.AccessorRegistries;
import com.lowdragmc.lowdraglib2.syncdata.accessor.direct.CustomDirectAccessor;
import com.startechnology.start_core.machine.redstone.RedstoneIndicatorRecord;
import com.startechnology.start_core.machine.threading.StarTThreadingCapableMachine.ThreadedRecipeExecution;

public class StarTSyncDataAccessors {

    public static void init() {
        AccessorRegistries.registerAccessor(CustomDirectAccessor.builder(ThreadedRecipeExecution.class, true)
                .codec(ThreadedRecipeExecution.CODEC)
                .streamCodec(ThreadedRecipeExecution.STREAM_CODEC)
                .customMark(ThreadedRecipeExecution::toMark, (mark, execution) -> execution.matches(mark))
                .build(), 100);

        AccessorRegistries.registerAccessor(CustomDirectAccessor.builder(RedstoneIndicatorRecord.class)
                .codec(RedstoneIndicatorRecord.CODEC)
                .streamCodec(ByteBufCodecs.fromCodec(RedstoneIndicatorRecord.CODEC))
                .build(), 100);
    }
}
