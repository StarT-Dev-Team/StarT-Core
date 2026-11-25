package com.startechnology.start_core.integration.ldlib;

import com.lowdragmc.lowdraglib2.plugin.ILDLibPlugin;
import com.lowdragmc.lowdraglib2.plugin.LDLibPlugin;
import com.startechnology.start_core.api.syncdata.StarTSyncDataAccessors;

@LDLibPlugin
public class StarTLDLibPlugin implements ILDLibPlugin {

    @Override
    public void onLoad() {
        StarTSyncDataAccessors.init();
    }
}
