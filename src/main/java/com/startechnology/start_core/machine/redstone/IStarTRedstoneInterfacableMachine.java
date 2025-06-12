package com.startechnology.start_core.machine.redstone;

import java.util.List;

public interface IStarTRedstoneInterfacableMachine {
    // Get possible indicator values of this machine
    // required because of ui packet shennanigans
    // assumes >1 string is returned.
    public List<String> getIndicators();
}