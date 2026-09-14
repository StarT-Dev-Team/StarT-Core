package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.fluids.FluidBuilder;

import static com.startechnology.start_core.materials.StarTMaterialHelpers.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.DISABLE_DECOMPOSITION;

public class StarTBacteriaLiquids {

    public static void register() {
        Fermentibacter = registerStartCoreMaterial("fermentibacter_solvis")
                .liquid(new FluidBuilder())
                .color(0xfd8151)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Xylopseudomonas = registerStartCoreMaterial("xylopseudomonas_creosotica")
                .liquid(new FluidBuilder())
                .color(0x815b33)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Petrospirillum = registerStartCoreMaterial("petrospirillum_solvans")
                .liquid(new FluidBuilder())
                .color(0x4b4b4b)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Octanivorax = registerStartCoreMaterial("octanivorax_sorbitolens")
                .liquid(new FluidBuilder())
                .color(0x913e3d)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Bituminimonas = registerStartCoreMaterial("bituminimonas_combustilis")
                .liquid(new FluidBuilder())
                .color(0xb9ad9f)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Carbanogasibacter = registerStartCoreMaterial("carbanogasibacter_volatilis")
                .liquid(new FluidBuilder())
                .color(0x80a466)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();
    }
}
