package com.startechnology.start_core.api.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class StarTCapability {
        public static final Capability<IStarTDreamLinkNetworkMachine> CAPABILITY_DREAM_LINK_NETWORK_MACHINE = CapabilityManager
            .get(new CapabilityToken<>() {});
}
