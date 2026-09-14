package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.startechnology.start_core.elements.StarTElements;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.startechnology.start_core.materials.StarTMaterialHelpers.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Iron;

public class StarTPlasmas {

    public static void register() {
        Magmatic = registerGTCEuMaterial("magmatic")
                .liquid(new FluidBuilder().temperature(14600))
                .plasma()
                .components(Mystery, 1, Excited, 1, Iron, 1,
                        Excited, 1, Mystery, 1)
                .color(0xffd39a)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Voidic = registerGTCEuMaterial("voidic")
                .liquid(new FluidBuilder().temperature(25000))
                .plasma()
                .element(StarTElements.Voidic)
                .color(0x0f0233)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Preon = registerGTCEuMaterial("preon")
                .liquid(new FluidBuilder().temperature(48000))
                .plasma()
                .components(Mystery, 1)
                .color(0xcfb7fd)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Paradox = registerGTCEuMaterial("paradox")
                .liquid(new FluidBuilder().temperature(50000))
                .plasma()
                .components(Mystery, 1)
                .color(0xefe987)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();
    }
}
