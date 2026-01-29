package com.startechnology.start_core.machine;

import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;

public class StarTPartAbility {
    /* Enables the "Absolute Parallel Hatch" to be used on this machine */
    public static final PartAbility ABSOLUTE_PARALLEL_HATCH = new PartAbility("absolute_parallel_hatch");

    /* Enables a redstone interface to be used on this machine, though specific logic must be done in core */
    public static final PartAbility REDSTONE_INTERFACE = new PartAbility("redstone_interface");

    /* Enables the usage of threading with the controller in this machine */
    public static final PartAbility THREADING_CONTROLLER = new PartAbility("threading_controller");

    /* Enables the usage of modular terminals in this machine */
    public static final PartAbility MODULAR_TERMINAL = new PartAbility("modular_terminal");

    /* Enables the usage of modular nodes in this machine */
    public static final PartAbility MODULAR_NODE = new PartAbility("modular_node");

    /* NOTE: 
     * There should only ever be ONE modular node on a module machine
     * (doesn't make much sense to have multiple "link" spots)
     * 
     * It is UB to have more than one !!! Here be dragons. Thou art forewarned
     */

    /* Enables the usage of modular terminal interfaces in this machine */
    public static final PartAbility MODULAR_TERMINAL_INTERFACE = new PartAbility("modular_terminal_interface");

    /* Enables the usage of modular node interfaces in this machine */
    public static final PartAbility MODULAR_NODE_INTERFACE = new PartAbility("modular_node_interface");

    /* Enables the usage of modular terminal conduits (powered) in this machine */
    public static final PartAbility MODULAR_TERMINAL_CONDUIT_2A = new PartAbility("modular_terminal_conduit_2a");
    public static final PartAbility MODULAR_TERMINAL_CONDUIT_4A = new PartAbility("modular_terminal_conduit_4a");
    public static final PartAbility MODULAR_TERMINAL_CONDUIT_16A = new PartAbility("modular_terminal_conduit_16a");
    public static final PartAbility MODULAR_TERMINAL_CONDUIT_64A = new PartAbility("modular_terminal_conduit_64a");
    public static final PartAbility MODULAR_TERMINAL_CONDUIT_256A = new PartAbility("modular_terminal_conduit_256a");
    public static final PartAbility MODULAR_TERMINAL_CONDUIT_1024A = new PartAbility("modular_terminal_conduit_1024a");
    public static final PartAbility MODULAR_TERMINAL_CONDUIT_4096A = new PartAbility("modular_terminal_conduit_4096a");

    /* Enables the usage of modular node conduits (powered) in this machine */
    public static final PartAbility MODULAR_NODE_CONDUIT_2A = new PartAbility("modular_node_conduit_2a");
    public static final PartAbility MODULAR_NODE_CONDUIT_4A = new PartAbility("modular_node_conduit_4a");
    public static final PartAbility MODULAR_NODE_CONDUIT_16A = new PartAbility("modular_node_conduit_16a");
    public static final PartAbility MODULAR_NODE_CONDUIT_64A = new PartAbility("modular_node_conduit_64a");
    public static final PartAbility MODULAR_NODE_CONDUIT_256A = new PartAbility("modular_node_conduit_256a");
    public static final PartAbility MODULAR_NODE_CONDUIT_1024A = new PartAbility("modular_node_conduit_1024a");
    public static final PartAbility MODULAR_NODE_CONDUIT_4096A = new PartAbility("modular_node_conduit_4096a");

}
