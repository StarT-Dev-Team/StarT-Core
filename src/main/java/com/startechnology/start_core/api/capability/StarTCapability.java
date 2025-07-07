package com.startechnology.start_core.api.capability;

import com.startechnology.start_core.machine.abyssal_harvester.StarTAbyssalHarvesterMachine;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachine;
import com.startechnology.start_core.machine.redstone.StarTRedstoneInterfacePartMachine;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class StarTCapability {
    public static final Capability<IStarTDreamLinkNetworkMachine> CAPABILITY_DREAM_LINK_NETWORK_MACHINE = CapabilityManager
        .get(new CapabilityToken<>() {});

    public static final Capability<StarTHellForgeMachine> CAPABILITY_HELL_FORGE_MACHINE = CapabilityManager
        .get(new CapabilityToken<>() {});

    public static final Capability<StarTRedstoneInterfacePartMachine> CAPABILITY_REDSTONE_INTERFACE = CapabilityManager
        .get(new CapabilityToken<>() {});

    public static final Capability<StarTAbyssalHarvesterMachine> CAPABILITY_ABYSSAL_HARVESTER = CapabilityManager
        .get(new CapabilityToken<>() {});
}
