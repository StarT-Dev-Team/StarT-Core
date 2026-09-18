package com.startechnology.start_core.api.capability;

import com.startechnology.start_core.machine.dreamlink.StarTDreamLinkTransmissionMachine;

import net.minecraft.core.BlockPos;

public interface IStarTDreamLinkNetworkReceiveEnergy {

    /* Receive energy from the network returning how much was received */
    long receiveEnergy(long received);

    /* Location of this network device */
    BlockPos devicePos();

    /* Whether or not this machine can receive from this transmission tower */
    boolean canReceive(StarTDreamLinkTransmissionMachine tower, boolean checkDimension);
}
