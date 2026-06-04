package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.startechnology.start_core.StarTCore;

public class StarTHellForgeHeatingLiquids {
    //Main Fluids
    public static Material BlazingPhlogiston;
    public static Material IgniferousElixir;
    public static Material EmberheartNectar;
    public static Material FlamewakeSolvent;
    //Intermediate Fluids
    public static Material HellfireEssence;
    public static Material InfernumElixir;
    public static Material CorefireNectar;
    public static Material CinderbrewSolvent;
    //Waste
    public static Material InfernalTar;

    public static void register() {
        BlazingPhlogiston = new Material.Builder(StarTCore.resourceLocation("blazing_phlogiston"))
            .langValue("§6Blazing Phlogiston")
            .liquid(new FluidBuilder().temperature(125_000_000))
            .color(0xff4500)
            .formula("🔥🔥🔥🔥")
            .flags(MaterialFlags.DISABLE_DECOMPOSITION)
            .buildAndRegister();

        HellfireEssence = new Material.Builder(StarTCore.resourceLocation("hellfire_essence"))
            .langValue("§6Hellfire Essence")
            .liquid(new FluidBuilder().temperature(25_000_000))
            .color(0xce3700)
            .formula("🔥🔥🔥🔥+")
            .flags(MaterialFlags.DISABLE_DECOMPOSITION)
            .buildAndRegister();

        IgniferousElixir = new Material.Builder(StarTCore.resourceLocation("igniferous_elixir"))
            .langValue("§6Igniferous Elixir")
            .liquid(new FluidBuilder().temperature(100_000_000))
            .color(0xff6E40)
            .formula("🔥🔥🔥")
            .flags(MaterialFlags.DISABLE_DECOMPOSITION)
            .buildAndRegister();

        InfernumElixir = new Material.Builder(StarTCore.resourceLocation("infernum_elixir"))
            .langValue("§6Infernum Elixir")
            .liquid(new FluidBuilder().temperature(20_000_000))
            .color(0xbe502f)
            .formula("🔥🔥🔥+")
            .flags(MaterialFlags.DISABLE_DECOMPOSITION)
            .buildAndRegister();

        EmberheartNectar = new Material.Builder(StarTCore.resourceLocation("emberheart_nectar"))
            .langValue("§6Emberheart Nectar")
            .liquid(new FluidBuilder().temperature(75_000_000))
            .color(0xff3C28)
            .formula("🔥🔥")
            .flags(MaterialFlags.DISABLE_DECOMPOSITION)
            .buildAndRegister();

        CorefireNectar = new Material.Builder(StarTCore.resourceLocation("corefire_nectar"))
            .langValue("§6Corefire Nectar")
            .liquid(new FluidBuilder().temperature(15_000_000))
            .color(0xba2312)
            .formula("🔥🔥+")
            .flags(MaterialFlags.DISABLE_DECOMPOSITION)
            .buildAndRegister();
    
        FlamewakeSolvent = new Material.Builder(StarTCore.resourceLocation("flamewake_solvent"))
            .langValue("§6Flamewake Solvent")
            .liquid(new FluidBuilder().temperature(50_000_000))
            .color(0xff9933)
            .formula("🔥")
            .flags(MaterialFlags.DISABLE_DECOMPOSITION)
            .buildAndRegister();

        CinderbrewSolvent = new Material.Builder(StarTCore.resourceLocation("cinderbrew_solvent"))
            .langValue("§6Cinderbrew Solvent")
            .liquid(new FluidBuilder().temperature(10_000_000))
            .color(0xdd7208)
            .formula("🔥+")
            .flags(MaterialFlags.DISABLE_DECOMPOSITION)
            .buildAndRegister();

        InfernalTar = new Material.Builder(StarTCore.resourceLocation("infernal_tar"))
            .langValue("§8Infernal Tar")
            .liquid(new FluidBuilder().temperature(50_000))
            .color(0x3e0000)
            .formula("🔥-")
            .flags(MaterialFlags.DISABLE_DECOMPOSITION)
            .buildAndRegister();
    }
}
