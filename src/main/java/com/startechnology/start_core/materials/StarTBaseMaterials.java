package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.data.chemical.material.properties.BlastProperty;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.startechnology.start_core.elements.StarTElements;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.*;
import static com.startechnology.start_core.materials.StarTMaterialHelpers.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

public class StarTBaseMaterials {

    public static void register() {
        Latex = registerGTCEuMaterial("latex")
                .polymer()
                .color(0xcfbdac)
                .flags(GENERATE_PLATE, DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Osthendah = registerGTCEuMaterial("osthendah")
                .ingot()
                .fluid()
                .components(Osmium, 1, Ruthenium, 1, Naquadah, 2)
                .color(0x9279a3)
                .iconSet(BRIGHT)
                .blast(b -> b.temp(7050, BlastProperty.GasTier.HIGHER).blastStats(VA[LuV], 1200))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_BOLT_SCREW, GENERATE_ROUND,
                        GENERATE_LONG_ROD, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_RING, GENERATE_FOIL,
                        GENERATE_DENSE)
                .buildAndRegister();

        NeutroniumSiliconCarbide = registerGTCEuMaterial("neutronium_silicon_carbide")
                .langValue("Neutronium-Silicon Carbide")
                .ingot()
                .components(Neutronium, 2, SiliconCarbide, 7, NiobiumNitride,
                        3, Graphene, 3)
                .color(0xcfcab8)
                .iconSet(DULL)
                .blast(b -> b.temp(5000, BlastProperty.GasTier.HIGHEST).blastStats(VA[UHV], 1800))
                .flags(GENERATE_FOIL, DISABLE_DECOMPOSITION, DISABLE_ALLOY_BLAST)
                .buildAndRegister();

        NetherStarConcentrate = registerGTCEuMaterial("nether_star_concentrate")
                .fluid()
                .components(Excited, 1, Star, 1, Excited, 1)
                .color(0xeeeeee)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        DissipatedHellishConcentrate = registerGTCEuMaterial("dissipated_hellish_concentrate")
                .fluid()
                .components(Mystery, 1)
                .color(0x8da589)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        HellishConcentrate = registerGTCEuMaterial("hellish_concentrate")
                .fluid()
                .components(Mystery, 1)
                .color(0x66a574)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Aurourium = registerGTCEuMaterial("aurourium")
                .ingot()
                .fluid()
                .element(StarTElements.Aurourium)
                .color(0x5d44de)
                .secondaryColor(0xde44ce)
                .iconSet(SHINY)
                .flags(DISABLE_DECOMPOSITION, GENERATE_FINE_WIRE, NO_SMELTING)
                .buildAndRegister();

        BorealicConcentrate = registerGTCEuMaterial("borealic_concentrate")
                .liquid(new FluidBuilder().customStill())
                .components(Aurourium, 1, Stellarium, 15)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        BorealicSteel = registerGTCEuMaterial("borealic_steel")
                .ingot()
                .fluid()
                .plasma()
                .components(Prismalium, 2, RoseGold, 4, Aurourium, 8,
                        TritanSteel, 2, AncientNetherite, 1, BorealicConcentrate,
                        3)
                .color(0x8f7090)
                .secondaryColor(0x70907c)
                .iconSet(SHINY)
                .blast(b -> b.temp(18880, BlastProperty.GasTier.HIGHEST).blastStats(VA[UXV], 600))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_BOLT_SCREW, GENERATE_DENSE,
                        GENERATE_LONG_ROD, GENERATE_GEAR, GENERATE_SMALL_GEAR, DISABLE_DECOMPOSITION,
                        DISABLE_ALLOY_BLAST)
                .buildAndRegister();

        CeriumTritelluride = registerGTCEuMaterial("cerium_tritelluride")
                .ingot()
                .fluid()
                .components(Cerium, 1, Tellurium, 3)
                .color(0x6d8b5d)
                .iconSet(DULL)
                .blast(b -> b.temp(11699, BlastProperty.GasTier.HIGHEST).blastStats(VHA[UHV], 1800))
                .cableProperties(V[UEV], 6, 16, false)
                .flags(GENERATE_BOLT_SCREW, GENERATE_SPRING, GENERATE_SPRING_SMALL)
                .buildAndRegister();

        BecOg = registerGTCEuMaterial("bec_og")
                .langValue("Oganesson Stabilized Bose-Einstein Condensate")
                .liquid(new FluidBuilder().temperature(1)) // 0.0001 K
                .components(Oganesson, 1)
                .color(0xbfacff)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SuperstateHelium3 = registerGTCEuMaterial("superstate_helium_3")
                .liquid(new FluidBuilder().temperature(2))
                .components(Helium3, 1)
                .color(0xedfaf5)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        MythrolicAlloy = registerGTCEuMaterial("mythrolic_alloy")
                .ingot()
                .fluid()
                .plasma()
                .components(Mythril, 5, HSSS, 4, Darmstadtium, 2)
                .color(0x30956c)
                .iconSet(SHINY)
                .blast(b -> b.temp(18550, BlastProperty.GasTier.HIGHEST).blastStats(VA[UEV], 600))
                .fluidPipeProperties(120000, 6000, true, true, true, true)
                .flags(GENERATE_PLATE, GENERATE_DENSE, GENERATE_FRAME, GENERATE_ROD, GENERATE_BOLT_SCREW,
                        GENERATE_ROUND, GENERATE_LONG_ROD, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROTOR,
                        GENERATE_RING, GENERATE_FOIL, DISABLE_DECOMPOSITION, DISABLE_ALLOY_BLAST)
                .buildAndRegister();

        StariumAlloy = registerGTCEuMaterial("starium_alloy")
                .ingot()
                .fluid()
                .plasma()
                .components(NetherStarConcentrate, 4, Trinaquadalloy, 2,
                        Estalt, 2)
                .color(0x2253d2)
                .iconSet(SHINY)
                .blast(b -> b.temp(18200, BlastProperty.GasTier.HIGHEST).blastStats(VA[UEV], 600))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_BOLT_SCREW, GENERATE_ROUND,
                        GENERATE_LONG_ROD, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_RING, DISABLE_DECOMPOSITION,
                        DISABLE_ALLOY_BLAST, GENERATE_FOIL, GENERATE_DENSE)
                .buildAndRegister();

        EnrichedPallaroviumAlloy = registerGTCEuMaterial("enriched_pallarovium_alloy")
                .ingot()
                .fluid()
                .plasma()
                .components(Seaborgium, 2, Palladium, 8, EnrichedEstalt,
                        3, Flerovium, 4)
                .color(0x73022b)
                .iconSet(DULL)
                .blast(b -> b.temp(17950, BlastProperty.GasTier.HIGHEST).blastStats(VA[UEV], 600))
                .cableProperties(V[UEV], 32, 0, true)
                .flags(DISABLE_DECOMPOSITION, DISABLE_ALLOY_BLAST, GENERATE_FINE_WIRE, GENERATE_FRAME)
                .buildAndRegister();

        AstatiumBioselexCarbonite = registerGTCEuMaterial("astatium_bioselex_carbonite")
                .ingot()
                .fluid()
                .components(Astatine, 1, Bismuth, 2, Selenium, 3,
                        Thallium, 2, Sulfur, 4, Carbon, 1)
                .color(0x305f84)
                .iconSet(DULL)
                .blast(b -> b.temp(13475, BlastProperty.GasTier.HIGHEST).blastStats(VA[UV], 3500))
                .cableProperties(V[UEV], 3, 16, false)
                .flags(GENERATE_SPRING, DISABLE_DECOMPOSITION)
                .buildAndRegister();

        PoloniumBismide = registerGTCEuMaterial("polonium_bismide")
                .langValue("Polonium-Bismide")
                .ingot()
                .fluid()
                .components(Polonium, 1, Bismuth, 1)
                .color(0x016038)
                .iconSet(DULL)
                .blast(b -> b.temp(14400, BlastProperty.GasTier.HIGHEST).blastStats(VHA[UEV], 1800))
                .cableProperties(V[UIV], 5, 24, false)
                .flags(GENERATE_FINE_WIRE, GENERATE_BOLT_SCREW, GENERATE_SPRING, GENERATE_SPRING_SMALL)
                .buildAndRegister();

        Diamane = registerGTCEuMaterial("diamane")
                .ingot()
                .components(Carbon, 1)
                .color(0x62777a)
                .blast(b -> b.temp(4000, BlastProperty.GasTier.LOW).blastStats(VA[LV], 1))
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        IridiumIvOxide = registerGTCEuMaterial("iridium_iv_oxide")
                .langValue("Iridium(IV) Oxide")
                .dust()
                .components(Iridium, 1, Oxygen, 2)
                .color(0xbeded9)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        BismuthIiiOxide = registerGTCEuMaterial("bismuth_iii_oxide")
                .langValue("Bismuth(III) Oxide")
                .dust()
                .components(Bismuth, 2, Oxygen, 3)
                .color(0xd5e5dc)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        BismuthIridate = registerGTCEuMaterial("bismuth_iridate")
                .ingot()
                .components(Bismuth, 2, Iridium, 2, Oxygen, 7)
                .color(0x68cf93)
                .blast(b -> b.temp(8600, BlastProperty.GasTier.HIGH).blastStats(VA[ZPM], 1080))
                .flags(GENERATE_FOIL, DISABLE_DECOMPOSITION, DISABLE_ALLOY_BLAST)
                .buildAndRegister();

        DragonBreath = registerGTCEuMaterial("dragon_breath")
                .liquid(new FluidBuilder().customStill())
                .components(Dragon, 1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        PureDragonBreath = registerGTCEuMaterial("pure_dragon_breath")
                .liquid(new FluidBuilder().customStill())
                .components(Excited, 1, Dragon, 1,
                        Excited, 1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        IndiumTinOxide = registerGTCEuMaterial("indium_tin_oxide")
                .dust()
                .components(Indium, 2, Tin, 2, Oxygen, 3)
                .color(0xa1c1e0)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        HafnideItoCeramic = registerGTCEuMaterial("hafnide_ito_ceramic")
                .ingot()
                .fluid()
                .components(Hafnium, 4, Tantalum, 5, Carbon, 5, Indium,
                        2, Tin, 2, Oxygen, 3)
                .color(0x798ca5)
                .iconSet(DULL)
                .blast(b -> b.temp(14520, BlastProperty.GasTier.HIGHEST).blastStats(VA[UHV], 3200))
                .cableProperties(V[UIV], 2, 36, false)
                .flags(GENERATE_SPRING, DISABLE_DECOMPOSITION, DISABLE_ALLOY_BLAST, GENERATE_RING)
                .buildAndRegister();

        RheniumSuperCompositeAlloy = registerGTCEuMaterial("rhenium_super_composite_alloy")
                .langValue("Rhenium Super-Composite Alloy")
                .ingot()
                .fluid()
                .plasma()
                .components(Rhenium, 4, WeaponGradeNaquadah, 2,
                        MercuryBariumCalciumCuprate, 7, TitaniumCarbide, 2,
                        Samarium, 1)
                .color(0xa78b72)
                .iconSet(DULL)
                .blast(b -> b.temp(18850, BlastProperty.GasTier.HIGHEST).blastStats(VA[UXV], 600))
                .cableProperties(V[UIV], 40, 0, true)
                .flags(DISABLE_DECOMPOSITION, DISABLE_ALLOY_BLAST, GENERATE_FINE_WIRE, GENERATE_BOLT_SCREW)
                .buildAndRegister();

        AbyssalAlloy = registerGTCEuMaterial("abyssal_alloy")
                .ingot()
                .fluid()
                .plasma()
                .components(Xeproda, 5, BlueAlloy, 3, Void,
                        4, Flerovium, 1, Zapolgium, 1)
                .color(0x1c0932)
                .iconSet(SHINY)
                .blast(b -> b.temp(18685, BlastProperty.GasTier.HIGHEST).blastStats(VA[UIV], 600))
                .cableProperties(V[UEV], 2, 4, false)
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_BOLT_SCREW, GENERATE_ROUND,
                        GENERATE_LONG_ROD, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_RING, DISABLE_DECOMPOSITION,
                        GENERATE_ROTOR, DISABLE_ALLOY_BLAST)
                .buildAndRegister();

        ChaotixicAlloy = registerGTCEuMaterial("chaotixic_alloy")
                .ingot()
                .fluid()
                .plasma()
                .components(Rhexis, 6, Stellite100, 2, Hafnium, 1,
                        Electrum, 12, VanadiumSteel, 3)
                .color(0xa09265)
                .iconSet(SHINY)
                .blast(b -> b.temp(18795, BlastProperty.GasTier.HIGHEST).blastStats(VA[UIV], 600))
                .fluidPipeProperties(250000, 30, true, true, true, true)
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_BOLT_SCREW, GENERATE_ROUND,
                        GENERATE_LONG_ROD, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROTOR, GENERATE_RING,
                        GENERATE_FOIL, DISABLE_DECOMPOSITION, DISABLE_ALLOY_BLAST)
                .buildAndRegister();

        OhmderbluxAlloy = registerGTCEuMaterial("ohmderblux_alloy")
                .ingot()
                .fluid()
                .plasma()
                .components(Chalyblux, 5, MaragingSteel300, 2, Zirconium,
                        4, Glowstone, 9, Ultimet, 3)
                .color(0xd0b660)
                .iconSet(SHINY)
                .blast(b -> b.temp(18590, BlastProperty.GasTier.HIGHEST).blastStats(VA[UIV], 600))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_BOLT_SCREW, GENERATE_ROUND,
                        GENERATE_LONG_ROD, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_RING, DISABLE_DECOMPOSITION,
                        DISABLE_ALLOY_BLAST, GENERATE_DENSE)
                .buildAndRegister();

        DraconicEnrichmentSerum = registerGTCEuMaterial("draconic_enrichment_serum")
                .fluid()
                .color(0xac97c5)
                .buildAndRegister();

        Draconyallium = registerGTCEuMaterial("draconyallium")
                .ingot()
                .fluid()
                .plasma()
                .components(Dragon, 1, Duranium, 68, Silver, 20,
                        Oxygen, 94, Nitrogen, 76, Dragon, 1)
                .color(0x5e0b75)
                .secondaryColor(0x7817ec)
                .iconSet(SHINY)
                .blast(b -> b.temp(18880, BlastProperty.GasTier.HIGHEST).blastStats(VA[UXV], 600))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_DENSE, GENERATE_LONG_ROD, GENERATE_GEAR,
                        GENERATE_FOIL, GENERATE_SMALL_GEAR, GENERATE_ROTOR, DISABLE_DECOMPOSITION, DISABLE_ALLOY_BLAST)
                .buildAndRegister();

        NetheriteTriselexOxide = registerGTCEuMaterial("netherite_triselex_oxide")
                .langValue("Netherite Triselex-Oxide")
                .dust()
                .fluid()
                .components(Netherite, 4, Selenium, 3, Oxygen, 8)
                .color(0xcfd9a3)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        NeutrindiumSolderingAlloy = registerGTCEuMaterial("neutrindium_soldering_alloy")
                .ingot()
                .fluid()
                .components(Neutronium, 2, Indium, 51, Tin, 6,
                        Darmstadtium, 4, Cadmium, 5, NaquadahEnriched, 4,
                        Hafnium, 2)
                .color(0xf8f2fa)
                .buildAndRegister();

        ThalliumAntimonide = registerGTCEuMaterial("thallium_antimonide")
                .dust()
                .components(Thallium, 1, Antimony, 1)
                .color(0xadc5e3)
                .buildAndRegister();

        LeptonDenseAkreyrium = registerGTCEuMaterial("lepton_dense_akreyrium")
                .liquid(new FluidBuilder().customStill())
                .components(UtopianAkreyrium, 1, Mystery, 1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        LeptonResonantThalliumAntimonide = registerGTCEuMaterial("lepton_resonant_thallium_antimonide")
                .langValue("Lepton-Resonant Thallium Antimonide")
                .ingot()
                .fluid()
                .components(Thallium, 1, Antimony, 1, Mystery, 1)
                .color(0x74638f)
                .iconSet(DULL)
                .blast(b -> b.temp(18250, BlastProperty.GasTier.HIGHEST).blastStats(VHA[UIV], 1800))
                .cableProperties(V[UXV], 7, 48, false)
                .flags(GENERATE_BOLT_SCREW, GENERATE_SPRING, GENERATE_SPRING_SMALL, DISABLE_ALLOY_BLAST,
                        DISABLE_DECOMPOSITION, GENERATE_FINE_WIRE)
                .buildAndRegister();

        RagingRimulatia = registerGTCEuMaterial("raging_rimulatia")
                .ingot()
                .fluid()
                .components(DracoAbyssal, 1, Riftic, 1)
                .color(0xe357f2)
                .secondaryColor(0x163f5e)
                .iconSet(SHINY)
                .rotorStats(14400, 560, 100.0f, 96000)
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_BOLT_SCREW, GENERATE_DENSE,
                        GENERATE_LONG_ROD, GENERATE_GEAR, GENERATE_SMALL_GEAR, DISABLE_DECOMPOSITION,
                        GENERATE_FINE_WIRE)
                .buildAndRegister();

        PrimordiallyStellarizedWeaponGradeNaquadah = registerGTCEuMaterial(
                "primordially_stellarized_weapon_grade_naquadah")
                .langValue("§5Primordially §bStellarized§f Weapon Grade Naquadah")
                .ingot()
                .fluid()
                .plasma()
                .components(Voidic, 1, StellarizedWeaponGradeNaquadah, 157,
                        Voidic, 1)
                .color(0x9881de)
                .secondaryColor(0x489957)
                .iconSet(SHINY)
                .blast(b -> b.temp(18880, BlastProperty.GasTier.HIGHEST).blastStats(VA[UXV], 600))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_DENSE, GENERATE_LONG_ROD, GENERATE_GEAR,
                        GENERATE_FOIL, GENERATE_SMALL_GEAR, GENERATE_ROTOR, GENERATE_FINE_WIRE, DISABLE_DECOMPOSITION,
                        DISABLE_ALLOY_BLAST)
                .buildAndRegister();

        AquariadicRimuliDragonix = registerGTCEuMaterial("aquariadic_rimuli_dragonix")
                .langValue("Aquariadic Rimuli-Dragonix")
                .gem()
                .components(PureDragonBreath, 5, AkreyriadicRunixium, 7, RagingRimulatia, 4)
                .color(0x16269e)
                .iconSet(GEM_VERTICAL)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Nyanium = registerGTCEuMaterial("nyanium")
                .ingot()
                .fluid()
                .plasma()
                .components(Aurourium, 7, UraniumRhodiumDinaquadide, 4, MagnesiumNitride,
                        1, PureNetherite, 2)
                .color(0xe4c6eb)
                .secondaryColor(0xa45ef5)
                .iconSet(SHINY)
                .blast(b -> b.temp(17290, BlastProperty.GasTier.HIGHEST).blastStats(VA[UEV], 600))
                .fluidPipeProperties(78500, 2500, true, true, true, true)
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_DENSE, GENERATE_LONG_ROD, GENERATE_GEAR,
                        GENERATE_FOIL, GENERATE_SMALL_GEAR, GENERATE_ROTOR, DISABLE_DECOMPOSITION, DISABLE_ALLOY_BLAST)
                .buildAndRegister();
    }
}
