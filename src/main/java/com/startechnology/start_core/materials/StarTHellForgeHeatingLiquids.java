package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.HazardProperty;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.gregtechceu.gtceu.common.data.GTElements;
import com.gregtechceu.gtceu.common.data.GTMedicalConditions;
import com.startechnology.start_core.StarTCore;

public class StarTHellForgeHeatingLiquids {
    public static Material BlazingPhlogiston;
    public static Material IgniferousElixir;
    public static Material EmberheartNectar;
    public static Material FlamewakeSolvent;
    
    public static void register() {
        BlazingPhlogiston = new Material.Builder(StarTCore.resourceLocation("blazing_phlogiston"))
            .liquid(new FluidBuilder().temperature(125_000_000))
            .color(0xFF4500)
            .formula("🔥🔥🔥🔥")
            .flags(MaterialFlags.DISABLE_DECOMPOSITION)
            .buildAndRegister();

        IgniferousElixir = new Material.Builder(StarTCore.resourceLocation("igniferous_elixir"))
            .liquid(new FluidBuilder().temperature(100_000_000))
            .color(0xFF6E40)
            .formula("🔥🔥🔥")
            .flags(MaterialFlags.DISABLE_DECOMPOSITION)
            .buildAndRegister();

        EmberheartNectar = new Material.Builder(StarTCore.resourceLocation("emberheart_nectar"))
            .liquid(new FluidBuilder().temperature(75_000_000))
            .color(0xFF3C28)
            .formula("🔥🔥")
            .flags(MaterialFlags.DISABLE_DECOMPOSITION)
            .buildAndRegister();
    
        FlamewakeSolvent = new Material.Builder(StarTCore.resourceLocation("flamewake_solvent"))
            .liquid(new FluidBuilder().temperature(50_000_000))
            .color(0xFF9933)
            .formula("🔥")
            .flags(MaterialFlags.DISABLE_DECOMPOSITION)
            .buildAndRegister();
    }
}
