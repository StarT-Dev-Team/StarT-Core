package com.startechnology.start_core.materials;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.startechnology.start_core.materials.StarTMaterialHelpers.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

public class StarTAgriculturalMaterials {

    public static void register() {
        RawSilkwormOil = registerGTCEuMaterial("raw_silkworm_oil")
                .fluid()
                .color(0x8b5a2b)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        RefinedSilkwormOil = registerGTCEuMaterial("refined_silkworm_oil")
                .fluid()
                .color(0xf5de93)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SilkwormGel = registerGTCEuMaterial("silkworm_gel")
                .fluid()
                .color(0xcdbe86)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        NutrientRichFertilizerSolution = registerGTCEuMaterial("nutrient_rich_fertilizer_solution")
                .fluid()
                .components(Phosphate, 1, Bone, 1, Water, 9, NpkSolution, 1)
                .color(0xb5b9c1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SeaweedOil = registerGTCEuMaterial("seaweed_oil")
                .fluid()
                .components(Carbon, 1)
                .color(0x3fbf3f)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        LiquefiedNutrientPaste = registerGTCEuMaterial("liquefied_nutrient_paste")
                .fluid()
                .color(0x8a8e96)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        BiostimulatingMixture = registerGTCEuMaterial("biostimulating_mixture")
                .fluid()
                .components(SilicicAcid, 2, SeaweedOil, 4, LiquefiedNutrientPaste, 5, Mutagen, 1, Glycerol, 3)
                .color(0x72a677)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();
    }
}
