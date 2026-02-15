package com.startechnology.start_core.api.syncdata;


import static com.lowdragmc.lowdraglib.syncdata.TypedPayloadRegistries.*;

import com.lowdragmc.lowdraglib.syncdata.IAccessor;
import com.lowdragmc.lowdraglib.syncdata.payload.FriendlyBufPayload;
import com.startechnology.start_core.machine.redstone.StarTRedstoneIndicatorRecord;
import com.startechnology.start_core.machine.threading.StarTThreadingCapableMachine;

public class StarTSyncDataAccessors {

    public static final IAccessor START_THREADED_RECIPE_ACCESSOR_TYPE = new StarTThreadingCapableMachine.ThreadedRecipeExecutionAccessor();
    public static final IAccessor START_REDSTONE_INDICATOR_RECORD_ACCESSOR_TYPE = new StarTRedstoneIndicatorRecord.StarTRedstoneIndicatorRecordAccessor();


    /* Register all the typed paylod registry accessors here */
    public static void init() {
        register(FriendlyBufPayload.class, FriendlyBufPayload::new, START_THREADED_RECIPE_ACCESSOR_TYPE, 100);
        register(FriendlyBufPayload.class, FriendlyBufPayload::new, START_REDSTONE_INDICATOR_RECORD_ACCESSOR_TYPE, 100);
    }
}
