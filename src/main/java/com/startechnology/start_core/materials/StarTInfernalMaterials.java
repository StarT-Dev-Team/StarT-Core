package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.fluids.FluidBuilder;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.startechnology.start_core.materials.StarTMaterialHelpers.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;

public class StarTInfernalMaterials {

    public static void register() {
        CrudeInfernalConcentrate = registerGTCEuMaterial("crude_infernal_concentrate")
                .liquid(new FluidBuilder().temperature(40000))
                .components(Mystery, 1)
                .color(0x6e1a00)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        InfernalConcentrate = registerGTCEuMaterial("infernal_concentrate")
                .liquid(new FluidBuilder().temperature(45000))
                .components(Mystery, 1)
                .color(0xb02e00)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SuperheatedInfernalConcentrate = registerGTCEuMaterial("superheated_infernal_concentrate")
                .liquid(new FluidBuilder().temperature(50000))
                .components(Mystery, 1)
                .color(0xff6b00)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SubStellarInfernalConcentrate = registerGTCEuMaterial("sub_stellar_infernal_concentrate")
                .langValue("Sub-Stellar Infernal Concentrate")
                .liquid(new FluidBuilder().temperature(75000))
                .components(Mystery, 1)
                .color(0xfefbc6)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SuperStellarInfernalConcentrate = registerGTCEuMaterial("super_stellar_infernal_concentrate")
                .langValue("Super-Stellar Infernal Concentrate")
                .liquid(new FluidBuilder().temperature(100000))
                .components(Mystery, 1)
                .color(0xf4faff)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        HyperStellarInfernalConcentrate = registerGTCEuMaterial("hyper_stellar_infernal_concentrate")
                .langValue("Hyper-Stellar Infernal Concentrate")
                .liquid(new FluidBuilder().temperature(125000))
                .components(Mystery, 1)
                .color(0x9cd7ff)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        // Magma lines
        HighlyUnstableNetherMagma = registerGTCEuMaterial("highly_unstable_nether_magma")
                .langValue("Highly Unstable §4Nether§r Magma")
                .liquid(new FluidBuilder().temperature(9001))
                .components(Mystery, 1)
                .color(0xffa025)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        DebrisRichNetherMagma = registerGTCEuMaterial("debris_rich_nether_magma")
                .langValue("Debris-Rich §4Nether§r Magma")
                .liquid(new FluidBuilder().temperature(7600))
                .components(Mystery, 1)
                .color(0x6c3628)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        MythrillicNetherMagma = registerGTCEuMaterial("mythrillic_nether_magma")
                .langValue("§3Mythrillic§r §4Nether§r Magma")
                .liquid(new FluidBuilder().temperature(9299))
                .components(Mystery, 1, Mythril, 1,
                        Mystery, 1)
                .color(0x238383)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        AdamantamiteNetherMagma = registerGTCEuMaterial("adamantamite_nether_magma")
                .langValue("§6Adamantamite§r §4Nether§r Magma")
                .liquid(new FluidBuilder().temperature(11299))
                .components(Mystery, 1, Adamantine, 1,
                        Mystery, 1)
                .color(0x826944)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        EstaltadyneNetherMagma = registerGTCEuMaterial("estaltadyne_nether_magma")
                .langValue("§cEstaltadyne§r §4Nether§r Magma")
                .liquid(new FluidBuilder().temperature(10299))
                .components(Mystery, 1, Estalt, 1,
                        Mystery, 1)
                .color(0xa92323)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        MysticalNetherMagma = registerGTCEuMaterial("mystical_nether_magma")
                .langValue("§o§dMystical§r §4Nether§r Magma")
                .liquid(new FluidBuilder().temperature(11600))
                .components(Mystery, 1, Adamantine, 1,
                        Mystery, 1, Estalt, 1,
                        Mystery, 1, Mythril, 1,
                        Mystery, 1)
                .color(0xf26b87)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        EnrichedMysticalConcentrate = registerGTCEuMaterial("enriched_mystical_concentrate")
                .langValue("Enriched §o§dMystical§r Concentrate")
                .liquid(new FluidBuilder().temperature(1260))
                .components(Mystery, 1, Adamantine, 1,
                        Mystery, 1, EnrichedEstalt, 1,
                        Mystery, 1, Mythril, 1,
                        Mystery, 1)
                .color(0xf26b87)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();
    }
}
