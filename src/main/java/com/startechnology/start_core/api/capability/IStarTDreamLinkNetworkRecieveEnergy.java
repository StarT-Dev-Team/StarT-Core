package com.startechnology.start_core.api.capability;

import com.startechnology.start_core.machine.dreamlink.StarTDreamLinkTransmissionMachine;
import com.startechnology.start_core.machine.dreamlink.StarTDreamLinkTransmissionTowers;

import net.minecraft.core.BlockPos;

public interface IStarTDreamLinkNetworkRecieveEnergy {
    /* Recieve energy from the network returning how much was recieved */
    long recieveEnergy(long recieved);

    /* Location of this network device */
    BlockPos devicePos();

    /* Whether or not this machine can recieve from this transmission tower */
    boolean canRecieve(StarTDreamLinkTransmissionMachine tower, Boolean checkDimension);
}
