package com.startechnology.start_core.materials;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.startechnology.start_core.materials.StarTMaterialHelpers.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

public class StarTNuclearMaterials {

    public static void register() {
        Mox239Blend = registerGTCEuMaterial("mox239_blend")
                .langValue("MOX-239 Blend")
                .dust()
                .components(Uranium238, 2, Plutonium239, 2)
                .color(0x289a0a)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .buildAndRegister();

        Mox241Blend = registerGTCEuMaterial("mox241_blend")
                .langValue("MOX-241 Blend")
                .dust()
                .components(Uranium238, 2, Plutonium241, 2)
                .color(0x587c13)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .buildAndRegister();

        TpuBlend = registerGTCEuMaterial("tpu_blend")
                .langValue("TPU Blend")
                .dust()
                .components(Thorium, 2, Plutonium239, 2)
                .color(0x36aa18)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .buildAndRegister();

        Mox238Blend = registerGTCEuMaterial("mox238_blend")
                .langValue("MOX-238 Blend")
                .dust()
                .components(Plutonium238, 3, Californium252, 1)
                .color(0x4f2f04)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .buildAndRegister();

        EtuBlend = registerGTCEuMaterial("etu_blend")
                .langValue("ETU Blend")
                .dust()
                .components(Curium244, 2, Californium252, 1,
                        Americium241, 1)
                .color(0x3d5434)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .buildAndRegister();

        NqeBlend = registerGTCEuMaterial("nqe_blend")
                .langValue("NQE Blend")
                .dust()
                .components(PurifiedNaquadah, 2, Einsteinium253, 2)
                .color(0x3f3c18)
                .flags(DECOMPOSITION_BY_CENTRIFUGING)
                .buildAndRegister();
    }
}
