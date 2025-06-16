package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.startechnology.start_core.StarTCore;

public class StarTAbyssalHarvesterVoidFluids {

    public static Material CorruptAbyssalVoid;
    public static Material TaintedAbyssalVoid;
    public static Material PollutedAbyssalVoid;
    public static Material MuddiedAbyssalVoid;
    public static Material DillutedAbyssalVoid;
    public static Material FilteredAbyssalVoid;
    public static Material RefinedAbyssalVoid;
    public static Material ClarifiedAbyssalVoid;
    public static Material PurifiedAbyssalVoid;
    public static Material PristineAbyssalVoid;
    public static Material ImmaculateAbyssalVoid;
    public static Material AbsoluteAbyssalVoid;

    public static void register() {
        CorruptAbyssalVoid = new Material.Builder(StarTCore.resourceLocation("corrupt_abyssal_void"))
            .liquid()
            .color(0x2D1B14)
            .formula("?")
            .flags(MaterialFlags.DISABLE_DECOMPOSITION)
            .buildAndRegister();

        // TaintedAbyssalVoid = new Material.Builder(StarTCore.resourceLocation("blazing_phlogiston"))
        //     .liquid()
        //     .color(0x4A3426)
        //     .formula("?")
        //     .flags(MaterialFlags.DISABLE_DECOMPOSITION)
        //     .buildAndRegister();

        // PollutedAbyssalVoid = new Material.Builder(StarTCore.resourceLocation("blazing_phlogiston"))
        //     .liquid()
        //     .color(0x665544)
        //     .formula("?")
        //     .flags(MaterialFlags.DISABLE_DECOMPOSITION)
        //     .buildAndRegister();

        // MuddiedAbyssalVoid = new Material.Builder(StarTCore.resourceLocation("blazing_phlogiston"))
        //     .liquid()
        //     .color(0x7A6B5D)
        //     .formula("?")
        //     .flags(MaterialFlags.DISABLE_DECOMPOSITION)
        //     .buildAndRegister();

        // DillutedAbyssalVoid = new Material.Builder(StarTCore.resourceLocation("blazing_phlogiston"))
        //     .liquid()
        //     .color(0x8F8478)
        //     .formula("?")
        //     .flags(MaterialFlags.DISABLE_DECOMPOSITION)
        //     .buildAndRegister();

        // FilteredAbyssalVoid = new Material.Builder(StarTCore.resourceLocation("blazing_phlogiston"))
        //     .liquid()
        //     .color(0xA39D94)
        //     .formula("?")
        //     .flags(MaterialFlags.DISABLE_DECOMPOSITION)
        //     .buildAndRegister();

        // RefinedAbyssalVoid = new Material.Builder(StarTCore.resourceLocation("blazing_phlogiston"))
        //     .liquid()
        //     .color(0xB8B5B0)
        //     .formula("?")
        //     .flags(MaterialFlags.DISABLE_DECOMPOSITION)
        //     .buildAndRegister();

        // ClarifiedAbyssalVoid = new Material.Builder(StarTCore.resourceLocation("blazing_phlogiston"))
        //     .liquid()
        //     .color(0xCDCCCA)
        //     .formula("?")
        //     .flags(MaterialFlags.DISABLE_DECOMPOSITION)
        //     .buildAndRegister();

        // PurifiedAbyssalVoid = new Material.Builder(StarTCore.resourceLocation("blazing_phlogiston"))
        //     .liquid()
        //     .color(0xE2E1E0)
        //     .formula("?")
        //     .flags(MaterialFlags.DISABLE_DECOMPOSITION)
        //     .buildAndRegister();

        // PristineAbyssalVoid = new Material.Builder(StarTCore.resourceLocation("blazing_phlogiston"))
        //     .liquid()
        //     .color(0xF0F0F0)
        //     .formula("?")
        //     .flags(MaterialFlags.DISABLE_DECOMPOSITION)
        //     .buildAndRegister();

        // ImmaculateAbyssalVoid = new Material.Builder(StarTCore.resourceLocation("blazing_phlogiston"))
        //     .liquid()
        //     .color(0xF8F8F8)
        //     .formula("?")
        //     .flags(MaterialFlags.DISABLE_DECOMPOSITION)
        //     .buildAndRegister();

        // AbsoluteAbyssalVoid = new Material.Builder(StarTCore.resourceLocation("blazing_phlogiston"))
        //     .liquid()
        //     .color(0xFFFFFF)
        //     .formula("?")
        //     .flags(MaterialFlags.DISABLE_DECOMPOSITION)
        //     .buildAndRegister();
    }
    
    
}
