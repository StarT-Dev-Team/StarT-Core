package com.startechnology.start_core.integration.ldlib;

import com.lowdragmc.lowdraglib.plugin.ILDLibPlugin;
import com.lowdragmc.lowdraglib.plugin.LDLibPlugin;
import com.startechnology.start_core.api.syncdata.StarTSyncDataAccessors;

@LDLibPlugin
public class StarTLDLibPlugin implements ILDLibPlugin {

    @Override
    public void onLoad() {
        StarTSyncDataAccessors.init();
    }
}
