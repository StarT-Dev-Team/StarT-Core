package com.startechnology.start_core.api.syncdata;


import com.lowdragmc.lowdraglib.syncdata.IAccessor;
import com.lowdragmc.lowdraglib.syncdata.payload.FriendlyBufPayload;
import com.startechnology.start_core.machine.redstone.RedstoneIndicatorRecord;
import com.startechnology.start_core.machine.threading.StarTThreadingCapableMachine;

import static com.lowdragmc.lowdraglib.syncdata.TypedPayloadRegistries.register;
import static com.lowdragmc.lowdraglib.syncdata.TypedPayloadRegistries.registerSimple;

public class StarTSyncDataAccessors {

    public static final IAccessor START_THREADED_RECIPE_ACCESSOR = new StarTThreadingCapableMachine.ThreadedRecipeExecutionAccessor();

    public static void init() {
        register(FriendlyBufPayload.class, FriendlyBufPayload::new, START_THREADED_RECIPE_ACCESSOR, 100);
        registerSimple(RedstoneIndicatorRecord.Payload.class, RedstoneIndicatorRecord.Payload::new, RedstoneIndicatorRecord.class, 1);
    }

}
