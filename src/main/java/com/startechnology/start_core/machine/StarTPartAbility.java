package com.startechnology.start_core.machine;

import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;

public class StarTPartAbility {
    /* Enables the "Absolute Parallel Hatch" to be used on this machine */
    public static final PartAbility ABSOLUTE_PARALLEL_HATCH = new PartAbility("absolute_parallel_hatch");

    /* Enables a redstone interface to be used on this machine, though specific logic must be done in core */
    public static final PartAbility REDSTONE_INTERFACE = new PartAbility("redstone_interface");

    /* Enables the usage of threading with the controller in this machine */
    public static final PartAbility THREADING_CONTROLLER = new PartAbility("threading_controller");

    public static final PartAbility VACUUM_PUMP = new PartAbility("vacuum_pump");
}
