package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.data.chemical.material.properties.BlastProperty;
import com.startechnology.start_core.elements.*;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.*;
import static com.startechnology.start_core.materials.StarTMaterialHelpers.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

public class StarTElementMaterials {

    public static void register() {
        Mystery = registerGTCEuMaterial("mystery")
                .element(StarTElements.Mystery)
                .buildAndRegister();

        Star = registerGTCEuMaterial("star")
                .element(StarTElements.Star)
                .buildAndRegister();

        Dragon = registerGTCEuMaterial("dragon")
                .element(StarTElements.Dragon)
                .buildAndRegister();

        Excited = registerGTCEuMaterial("excited")
                .element(StarTElements.Excited)
                .buildAndRegister();

        Soul = registerGTCEuMaterial("soul")
                .element(StarTElements.Soul)
                .buildAndRegister();

        Riftic = registerGTCEuMaterial("riftic")
                .element(StarTElements.Riftic)
                .buildAndRegister();

        Faetic = registerGTCEuMaterial("faetic")
                .element(StarTElements.Faetic)
                .buildAndRegister();

        Akreyrium = registerGTCEuMaterial("akreyrium")
                .element(StarTElements.Akreyrium)
                .buildAndRegister();

        Uranium233 = registerGTCEuMaterial("uranium_233")
                .dust()
                .element(StarTElements.Uranium233)
                .color(0x4fbb4f)
                .flags(NO_SMELTING)
                .buildAndRegister();

        Plutonium238 = registerGTCEuMaterial("plutonium_238")
                .dust()
                .fluid()
                .element(StarTElements.Plutonium238)
                .color(0xc13d3d)
                .flags(NO_SMELTING)
                .buildAndRegister();

        Plutonium244 = registerGTCEuMaterial("plutonium_244")
                .dust()
                .element(StarTElements.Plutonium244)
                .color(0x951f1f)
                .flags(NO_SMELTING)
                .buildAndRegister();

        Americium241 = registerGTCEuMaterial("americium_241")
                .dust()
                .element(StarTElements.Americium241)
                .color(0x1e492f)
                .flags(NO_SMELTING)
                .buildAndRegister();

        Curium244 = registerGTCEuMaterial("curium_244")
                .dust()
                .element(StarTElements.Curium244)
                .color(0x6f4c46)
                .flags(NO_SMELTING)
                .buildAndRegister();

        Californium252 = registerGTCEuMaterial("californium_252")
                .dust()
                .element(StarTElements.Californium252)
                .color(0xa38783)
                .flags(NO_SMELTING)
                .buildAndRegister();

        Einsteinium253 = registerGTCEuMaterial("einsteinium_253")
                .dust()
                .element(StarTElements.Einsteinium253)
                .color(0xddbc4d)
                .flags(NO_SMELTING)
                .buildAndRegister();

        Xeproda = registerGTCEuMaterial("xeproda")
                .ingot()
                .fluid()
                .element(StarTElements.Xeproda)
                .color(0x1a0d00)
                .iconSet(DULL)
                .blast(b -> b.temp(15499, BlastProperty.GasTier.HIGHEST).blastStats(VA[UEV], 2700))
                .flags(GENERATE_FINE_WIRE)
                .buildAndRegister();

        Rhexis = registerGTCEuMaterial("rhexis")
                .ingot()
                .fluid()
                .element(StarTElements.Rhexis)
                .color(0x330000)
                .iconSet(DULL)
                .blast(b -> b.temp(15499, BlastProperty.GasTier.HIGHEST).blastStats(VHA[UIV], 2700))
                .buildAndRegister();

        Chalyblux = registerGTCEuMaterial("chalyblux")
                .ingot()
                .fluid()
                .element(StarTElements.Chalyblux)
                .color(0xffcccc)
                .iconSet(DULL)
                .blast(b -> b.temp(15499, BlastProperty.GasTier.HIGHEST).blastStats(VA[UEV], 2575))
                .buildAndRegister();

        Mythril = registerGTCEuMaterial("mythril")
                .langValue("§3Mythril§r")
                .ingot()
                .fluid()
                .element(StarTElements.Mythril)
                .color(0x006666)
                .iconSet(METALLIC)
                .blast(b -> b.temp(11299, BlastProperty.GasTier.HIGHEST).blastStats(VA[UHV], 2400))
                .flags(GENERATE_FOIL, GENERATE_GEAR, GENERATE_LONG_ROD, GENERATE_PLATE, GENERATE_ROD, GENERATE_ROTOR,
                        GENERATE_SMALL_GEAR, GENERATE_RING, GENERATE_FRAME)
                .buildAndRegister();

        Adamantine = registerGTCEuMaterial("adamantine")
                .langValue("§6Adamantine§r")
                .ingot()
                .fluid()
                .element(StarTElements.Adamantine)
                .color(0xe99700)
                .iconSet(METALLIC)
                .blast(b -> b.temp(13299, BlastProperty.GasTier.HIGHEST).blastStats(VHA[UEV], 2300))
                .flags(GENERATE_FOIL, GENERATE_GEAR, GENERATE_LONG_ROD, GENERATE_PLATE, GENERATE_ROD, GENERATE_ROTOR,
                        GENERATE_SMALL_GEAR, GENERATE_RING, GENERATE_FRAME, GENERATE_FINE_WIRE)
                .buildAndRegister();

        Estalt = registerGTCEuMaterial("estalt")
                .langValue("§cEstalt§r")
                .ingot()
                .fluid()
                .element(StarTElements.Estalt)
                .color(0xff5050)
                .iconSet(DULL)
                .blast(b -> b.temp(12299, BlastProperty.GasTier.HIGHEST).blastStats(VA[UHV], 2200))
                .flags(GENERATE_FOIL, GENERATE_GEAR, GENERATE_LONG_ROD, GENERATE_PLATE, GENERATE_ROD, GENERATE_ROTOR,
                        GENERATE_SMALL_GEAR, GENERATE_RING, GENERATE_FRAME)
                .buildAndRegister();

        EnrichedEstalt = registerGTCEuMaterial("enriched_estalt")
                .langValue("Enriched §c§oEstalt§r")
                .ingot()
                .fluid()
                .element(StarTElements.EnrichedEstalt)
                .color(0xe76c6c)
                .iconSet(RADIOACTIVE)
                .blast(b -> b.temp(12899, BlastProperty.GasTier.HIGHEST).blastStats(VA[UHV], 2500))
                .flags(GENERATE_FOIL, GENERATE_GEAR, GENERATE_LONG_ROD, GENERATE_PLATE, GENERATE_ROD, GENERATE_ROTOR,
                        GENERATE_SMALL_GEAR, GENERATE_RING, GENERATE_FRAME)
                .buildAndRegister();

        Calamatium = registerGTCEuMaterial("calamatium")
                .ingot()
                .fluid()
                .element(StarTElements.Calamatium)
                .color(0x660000)
                .iconSet(DULL)
                .blast(b -> b.temp(13199, BlastProperty.GasTier.HIGHEST).blastStats(VA[UHV], 2400))
                .flags(GENERATE_FOIL, GENERATE_GEAR, GENERATE_LONG_ROD, GENERATE_PLATE, GENERATE_ROD, GENERATE_ROTOR,
                        GENERATE_SMALL_GEAR, GENERATE_RING, GENERATE_FRAME)
                .buildAndRegister();

        Isovol = registerGTCEuMaterial("isovol")
                .ingot()
                .fluid()
                .element(StarTElements.Isovol)
                .color(0x290066)
                .iconSet(DULL)
                .blast(b -> b.temp(12999, BlastProperty.GasTier.HIGHEST).blastStats(VA[UHV], 2400))
                .flags(GENERATE_FOIL, GENERATE_GEAR, GENERATE_LONG_ROD, GENERATE_PLATE, GENERATE_ROD, GENERATE_ROTOR,
                        GENERATE_SMALL_GEAR, GENERATE_RING, GENERATE_FRAME)
                .buildAndRegister();

        Zapolgium = registerGTCEuMaterial("zapolgium")
                .ingot()
                .fluid()
                .element(StarTElements.Zapolgium)
                .color(0xcc00cc)
                .iconSet(DULL)
                .blast(b -> b.temp(10799, BlastProperty.GasTier.HIGHEST).blastStats(VA[UHV], 1600))
                .flags(GENERATE_PLATE, GENERATE_ROD, GENERATE_FRAME, GENERATE_LONG_ROD)
                .fluidPipeProperties(18000, 7200, true, true, true, true)
                .buildAndRegister();

        MagneticZapolgium = registerGTCEuMaterial("magnetic_zapolgium")
                .ingot()
                .components(Zapolgium, 1)
                .color(0xcc00cc)
                .iconSet(MAGNETIC)
                .flags(GENERATE_LONG_ROD, IS_MAGNETIC)
                .buildAndRegister();

        MagneticDysprosium = registerGTCEuMaterial("magnetic_dysprosium")
                .ingot()
                .components(Dysprosium, 1)
                .color(0x6a664b)
                .secondaryColor(0x423307)
                .iconSet(MAGNETIC)
                .flags(GENERATE_LONG_ROD, IS_MAGNETIC)
                .arcSmeltInto(Dysprosium)
                .oreSmeltInto(Dysprosium)
                .buildAndRegister();

        Echo = registerGTCEuMaterial("echo_r")
                .langValue("Echo Fluid")
                .fluid()
                .element(StarTElements.Echo)
                .color(0x003333)
                .buildAndRegister();

        RawVoid = registerGTCEuMaterial("raw_void")
                .ingot()
                .components(Echo, 1, Neutronium, 1)
                .color(0x006666)
                .iconSet(DULL)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Void = registerGTCEuMaterial("void")
                .ingot()
                .fluid()
                .components(Echo, 1, Neutronium, 1)
                .color(0x001a1a)
                .iconSet(DULL)
                .blast(b -> b.temp(10000, BlastProperty.GasTier.HIGHEST).blastStats(VA[UIV], 8000))
                .flags(GENERATE_ROD, GENERATE_FOIL, GENERATE_PLATE, GENERATE_LONG_ROD, GENERATE_FRAME,
                        DISABLE_DECOMPOSITION, DISABLE_ALLOY_BLAST, GENERATE_BOLT_SCREW, GENERATE_RING)
                .buildAndRegister();

        UtopianAkreyrium = registerGTCEuMaterial("utopian_akreyrium")
                .fluid()
                .element(StarTElements.Akreyrium)
                .color(0xffffff)
                .buildAndRegister();
    }
}
