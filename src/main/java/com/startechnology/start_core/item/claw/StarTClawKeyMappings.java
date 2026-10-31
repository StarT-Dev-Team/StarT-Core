package com.startechnology.start_core.item.claw;

import com.gregtechceu.gtceu.utils.input.SyncedKeyMapping;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraftforge.client.settings.KeyConflictContext;

public class StarTClawKeyMappings {

    public static SyncedKeyMapping CYCLE;

    public static void init() {
        CYCLE = SyncedKeyMapping.createConfigurable("key.start_core.claw_cycle", KeyConflictContext.IN_GAME,
                InputConstants.KEY_R);

        StarTClawSelectorNetwork.init();
    }
}
