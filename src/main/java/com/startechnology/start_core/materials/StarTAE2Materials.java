package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.data.chemical.material.properties.BlastProperty;
import com.startechnology.start_core.elements.StarTElements;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.*;
import static com.startechnology.start_core.materials.StarTMaterialHelpers.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

public class StarTAE2Materials {

    public static void register() {
        Skystone = registerGTCEuMaterial("skystone")
                .fluid()
                .element(StarTElements.Skystone)
                .color(0x414445)
                .buildAndRegister();

        Fluix = registerGTCEuMaterial("fluix")
                .element(StarTElements.Fluix)
                .flags(GENERATE_LENS)
                .iconSet(StarTMaterialIconSets.FLUIX)
                .buildAndRegister();

        SkySteel = registerGTCEuMaterial("sky_steel")
                .ingot()
                .fluid()
                .components(Skystone, 1, Steel, 2)
                .color(0xccffcc)
                .iconSet(METALLIC)
                .blast(b -> b.temp(1600, BlastProperty.GasTier.LOW).blastStats(VA[MV], 400))
                .flags(DISABLE_DECOMPOSITION, GENERATE_PLATE, GENERATE_ROD, GENERATE_FRAME)
                .buildAndRegister();

        GoldSkystoneAlloy = registerGTCEuMaterial("gold_skystone_alloy")
                .ingot()
                .fluid()
                .components(Skystone, 1, Gold, 2)
                .color(0xcfbe38)
                .secondaryColor(0x414445)
                .iconSet(METALLIC)
                .blast(b -> b.temp(1600, BlastProperty.GasTier.LOW).blastStats(VA[MV], 200))
                .flags(DISABLE_DECOMPOSITION, GENERATE_PLATE)
                .buildAndRegister();

        DiamondSkystoneAlloy = registerGTCEuMaterial("diamond_skystone_alloy")
                .ingot()
                .fluid()
                .components(Skystone, 1, Diamond, 2)
                .color(0x9bd6d8)
                .secondaryColor(0x414445)
                .iconSet(SHINY)
                .blast(b -> b.temp(1600, BlastProperty.GasTier.LOW).blastStats(VA[MV], 200))
                .flags(DISABLE_DECOMPOSITION, GENERATE_PLATE)
                .buildAndRegister();

        CertusQuartzSkystoneAlloy = registerGTCEuMaterial("certus_quartz_skystone_alloy")
                .ingot()
                .fluid()
                .components(Skystone, 1, CertusQuartz, 2)
                .color(0x67d6db)
                .secondaryColor(0x414445)
                .iconSet(DULL)
                .blast(b -> b.temp(1600, BlastProperty.GasTier.LOW).blastStats(VA[MV], 200))
                .flags(DISABLE_DECOMPOSITION, GENERATE_PLATE)
                .buildAndRegister();

        FluixSteel = registerGTCEuMaterial("fluix_steel")
                .ingot()
                .fluid()
                .components(Fluix, 1, Steel, 2)
                .color(0x8f5ccb)
                .iconSet(METALLIC)
                .blast(b -> b.temp(1900, BlastProperty.GasTier.MID).blastStats(VA[MV], 400))
                .flags(DISABLE_DECOMPOSITION, GENERATE_PLATE, GENERATE_ROD, GENERATE_FRAME, GENERATE_FOIL)
                .buildAndRegister();

        NetheriteGoldSkystoneAlloy = registerGTCEuMaterial("netherite_gold_skystone_alloy")
                .ingot()
                .fluid()
                .components(Netherite, 4, DiamondSkystoneAlloy, 2, GoldSkystoneAlloy, 1)
                .color(0x978b2d)
                .secondaryColor(0x0d0702)
                .iconSet(METALLIC)
                .blast(b -> b.temp(4000, BlastProperty.GasTier.HIGH).blastStats(VA[IV], 800))
                .flags(DISABLE_DECOMPOSITION, GENERATE_PLATE, GENERATE_ROD, GENERATE_FRAME)
                .buildAndRegister();

        NetheriteCertusQuartzSkystoneAlloy = registerGTCEuMaterial("netherite_certus_quartz_skystone_alloy")
                .ingot()
                .fluid()
                .components(Netherite, 4, DiamondSkystoneAlloy, 2, CertusQuartzSkystoneAlloy, 1)
                .color(0x396a6c)
                .secondaryColor(0x0d0702)
                .iconSet(DULL)
                .blast(b -> b.temp(4000, BlastProperty.GasTier.HIGH).blastStats(VA[IV], 800))
                .flags(DISABLE_DECOMPOSITION, GENERATE_PLATE, GENERATE_ROD, GENERATE_FRAME)
                .buildAndRegister();
    }
}
