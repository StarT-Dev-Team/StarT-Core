package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;

import static com.startechnology.start_core.materials.StarTMaterialHelpers.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;

public class StarTHellForgeHeatingLiquids {

    public static void register() {
        BlazingPhlogiston = registerStartCoreMaterial("blazing_phlogiston")
                .langValue("§6Blazing Phlogiston")
                .liquid(new FluidBuilder().temperature(125_000_000))
                .color(0xff4500)
                .formula("🔥🔥🔥🔥")
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .buildAndRegister();

        HellfireEssence = registerStartCoreMaterial("hellfire_essence")
                .langValue("§6Hellfire Essence")
                .liquid(new FluidBuilder().temperature(25_000_000))
                .color(0xce3700)
                .formula("🔥🔥🔥🔥+")
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .buildAndRegister();

        IgniferousElixir = registerStartCoreMaterial("igniferous_elixir")
                .langValue("§6Igniferous Elixir")
                .liquid(new FluidBuilder().temperature(100_000_000))
                .color(0xff6E40)
                .formula("🔥🔥🔥")
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .buildAndRegister();

        InfernumElixir = registerStartCoreMaterial("infernum_elixir")
                .langValue("§6Infernum Elixir")
                .liquid(new FluidBuilder().temperature(20_000_000))
                .color(0xbe502f)
                .formula("🔥🔥🔥+")
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .buildAndRegister();

        EmberheartNectar = registerStartCoreMaterial("emberheart_nectar")
                .langValue("§6Emberheart Nectar")
                .liquid(new FluidBuilder().temperature(75_000_000))
                .color(0xff3C28)
                .formula("🔥🔥")
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .buildAndRegister();

        CorefireNectar = registerStartCoreMaterial("corefire_nectar")
                .langValue("§6Corefire Nectar")
                .liquid(new FluidBuilder().temperature(15_000_000))
                .color(0xba2312)
                .formula("🔥🔥+")
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .buildAndRegister();

        FlamewakeSolvent = registerStartCoreMaterial("flamewake_solvent")
                .langValue("§6Flamewake Solvent")
                .liquid(new FluidBuilder().temperature(50_000_000))
                .color(0xff9933)
                .formula("🔥")
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .buildAndRegister();

        CinderbrewSolvent = registerStartCoreMaterial("cinderbrew_solvent")
                .langValue("§6Cinderbrew Solvent")
                .liquid(new FluidBuilder().temperature(10_000_000))
                .color(0xdd7208)
                .formula("🔥+")
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .buildAndRegister();

        InfernalTar = registerStartCoreMaterial("infernal_tar")
                .langValue("§8Infernal Tar")
                .liquid(new FluidBuilder().temperature(50_000))
                .color(0x3e0000)
                .formula("🔥-")
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .buildAndRegister();
    }
}
