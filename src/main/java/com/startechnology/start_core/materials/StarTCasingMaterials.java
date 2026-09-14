package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.data.chemical.material.properties.BlastProperty;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.*;
import static com.startechnology.start_core.materials.StarTMaterialHelpers.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

public class StarTCasingMaterials {

    public static void register() {
        AusteniticStainlessSteel304 = registerGTCEuMaterial("austenitic_stainless_steel_304")
                .ingot()
                .components(Steel, 35, Chromium, 10, Nickel, 4,
                        Manganese, 1, Silicon, 1)
                .color(0x800040)
                .iconSet(METALLIC)
                .blast(b -> b.temp(3500, BlastProperty.GasTier.LOW).blastStats(VA[EV], 1500))
                .flags(GENERATE_PLATE, GENERATE_ROD, GENERATE_FRAME)
                .buildAndRegister();

        Inconel625 = registerGTCEuMaterial("inconel_625")
                .ingot()
                .components(Nickel, 7, Chromium, 2, Steel, 1)
                .color(0xa3a375)
                .iconSet(SHINY)
                .blast(b -> b.temp(3500, BlastProperty.GasTier.LOW).blastStats(VA[EV], 1500))
                .flags(GENERATE_PLATE, GENERATE_ROD, GENERATE_FRAME)
                .buildAndRegister();

        Birmabright = registerGTCEuMaterial("birmabright")
                .ingot()
                .components(Aluminium, 7, Magnesium, 2, Manganese, 1)
                .color(0xbfbfbf)
                .iconSet(DULL)
                .blast(b -> b.temp(2200, BlastProperty.GasTier.LOW).blastStats(VA[MV], 1500))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD)
                .buildAndRegister();

        Duralumin = registerGTCEuMaterial("duralumin")
                .ingot()
                .components(Aluminium, 4, Copper, 3, Magnesium, 1,
                        Manganese, 1)
                .color(0x66ccff)
                .iconSet(DULL)
                .blast(b -> b.temp(2200, BlastProperty.GasTier.LOW).blastStats(VA[MV], 1500))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD)
                .buildAndRegister();

        Hydronalium = registerGTCEuMaterial("hydronalium")
                .ingot()
                .components(Aluminium, 6, Magnesium, 3, Manganese, 1)
                .color(0x660000)
                .iconSet(DULL)
                .blast(b -> b.temp(2200, BlastProperty.GasTier.LOW).blastStats(VA[MV], 1500))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD)
                .buildAndRegister();

        BerylliumAluminiumAlloy = registerGTCEuMaterial("beryllium_aluminium_alloy")
                .langValue("Beryllium-Aluminium Alloy")
                .ingot()
                .components(Beryllium, 7, Aluminium, 1)
                .color(0x006699)
                .iconSet(DULL)
                .blast(b -> b.temp(2200, BlastProperty.GasTier.LOW).blastStats(VA[MV], 1500))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD)
                .buildAndRegister();

        Elgiloy = registerGTCEuMaterial("elgiloy")
                .ingot()
                .components(Cobalt, 4, Chromium, 2, Nickel, 1, Steel, 1,
                        Molybdenum, 1, Manganese, 1)
                .color(0xff00ff)
                .iconSet(DULL)
                .blast(b -> b.temp(2200, BlastProperty.GasTier.LOW).blastStats(VA[MV], 1500))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD)
                .buildAndRegister();

        BerylliumBronze = registerGTCEuMaterial("beryllium_bronze")
                .ingot()
                .components(Copper, 10, Beryllium, 1)
                .color(0x003300)
                .iconSet(DULL)
                .blast(b -> b.temp(2200, BlastProperty.GasTier.LOW).blastStats(VA[MV], 1500))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD)
                .buildAndRegister();

        SiliconBronze = registerGTCEuMaterial("silicon_bronze")
                .ingot()
                .components(Copper, 32, Silicon, 2, Manganese, 1)
                .color(0x1a1a1a)
                .iconSet(DULL)
                .blast(b -> b.temp(2200, BlastProperty.GasTier.LOW).blastStats(VA[MV], 1500))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD)
                .buildAndRegister();

        Kovar = registerGTCEuMaterial("kovar")
                .ingot()
                .components(Iron, 18, Nickel, 11, Cobalt, 6)
                .color(0x000080)
                .iconSet(DULL)
                .blast(b -> b.temp(2200, BlastProperty.GasTier.LOW).blastStats(VA[MV], 1500))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD)
                .buildAndRegister();

        Zamak = registerGTCEuMaterial("zamak")
                .ingot()
                .components(Zinc, 1, Aluminium, 4, Copper, 3)
                .color(0x8c8c8c)
                .iconSet(DULL)
                .blast(b -> b.temp(2200, BlastProperty.GasTier.LOW).blastStats(VA[MV], 1500))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD)
                .buildAndRegister();

        Tumbaga = registerGTCEuMaterial("tumbaga")
                .ingot()
                .components(Copper, 20, Gold, 6, Silver, 1)
                .color(0xffdb4d)
                .iconSet(DULL)
                .blast(b -> b.temp(2200, BlastProperty.GasTier.LOW).blastStats(VA[MV], 1500))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD)
                .buildAndRegister();

        Manganin = registerGTCEuMaterial("manganin")
                .ingot()
                .components(Copper, 20, Manganese, 6, Nickel, 1)
                .color(0xf8aa92)
                .iconSet(DULL)
                .blast(b -> b.temp(2200, BlastProperty.GasTier.LOW).blastStats(VA[MV], 1500))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD)
                .buildAndRegister();

        GalvanizedSteel = registerGTCEuMaterial("galvanized_steel")
                .ingot()
                .components(Steel, 7, Zinc, 1)
                .color(0x999999)
                .iconSet(DULL)
                .blast(b -> b.temp(2200, BlastProperty.GasTier.LOW).blastStats(VA[MV], 1500))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD)
                .buildAndRegister();

        // Intermediate minerals
        ThalliumTungstate = registerGTCEuMaterial("thallium_tungstate")
                .dust()
                .components(Thallium, 2, Tungsten, 1, Oxygen, 4)
                .color(0xe3d18a)
                .iconSet(DULL)
                .buildAndRegister();

        TungstenTrioxide = registerGTCEuMaterial("tungsten_trioxide")
                .dust()
                .components(Tungsten, 1, Oxygen, 3)
                .color(0xadb426)
                .iconSet(DULL)
                .buildAndRegister();

        BoronNitride = registerGTCEuMaterial("boron_nitride")
                .dust()
                .components(Boron, 1, Nitrogen, 1)
                .color(0xd4c4a0)
                .iconSet(DULL)
                .buildAndRegister();

        BoronTrioxide = registerGTCEuMaterial("boron_trioxide")
                .dust()
                .components(Boron, 2, Oxygen, 3)
                .color(0xdacabb)
                .iconSet(DULL)
                .buildAndRegister();

        // Ultimate Multiblocks
        AstrenalloyNx = registerGTCEuMaterial("astrenalloy_nx")
                .langValue("Astrenalloy NX")
                .ingot()
                .fluid()
                .components(HastelloyX, 1, NaquadahEnriched, 4, Zirconium, 3,
                        TantalumCarbide, 6, Osmiridium, 4, BoronNitride, 3)
                .color(0x63478e)
                .iconSet(SHINY)
                .blast(b -> b.temp(8650, BlastProperty.GasTier.HIGHEST).blastStats(VHA[ZPM], 2100))
                .flags(GENERATE_PLATE, GENERATE_ROD, GENERATE_FRAME)
                .buildAndRegister();

        ThacoloyNq42x = registerGTCEuMaterial("thacoloy_nq_42x")
                .langValue("Thacoloy NQ-42X")
                .ingot()
                .fluid()
                .components(IncoloyMA956, 6, NaquadahEnriched, 4, NiobiumTitanium,
                        2, Osmiridium, 4, ThalliumTungstate, 4)
                .color(0x467624)
                .iconSet(SHINY)
                .blast(b -> b.temp(8800, BlastProperty.GasTier.HIGHEST).blastStats(VHA[ZPM], 1800))
                .flags(GENERATE_PLATE, GENERATE_ROD, GENERATE_FRAME)
                .buildAndRegister();

        TritanSteel = registerGTCEuMaterial("tritan_steel")
                .ingot()
                .fluid()
                .components(Tritanium, 7, MaragingSteel300, 3, NaquadahEnriched, 2,
                        TitaniumTungstenCarbide, 3, BoronNitride, 1)
                .color(0x9a445d)
                .secondaryColor(0x2d095a)
                .iconSet(METALLIC)
                .blast(b -> b.temp(8990, BlastProperty.GasTier.HIGHEST).blastStats(VHA[UV], 2400))
                .flags(GENERATE_PLATE, GENERATE_ROD, GENERATE_FRAME, GENERATE_SMALL_GEAR, GENERATE_GEAR, GENERATE_ROUND,
                        GENERATE_RING, GENERATE_BOLT_SCREW, GENERATE_LONG_ROD, GENERATE_FOIL, GENERATE_DENSE)
                .buildAndRegister();

        HafnideCeramicBase = registerGTCEuMaterial("hafnide_ceramic_base")
                .ingot()
                .fluid()
                .components(Hafnium, 4, TantalumCarbide, 5)
                .color(0x4f4f4f)
                .iconSet(DULL)
                .blast(b -> b.temp(12900, BlastProperty.GasTier.HIGHEST).blastStats(VA[UV], 970))
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        ExpetidalloyD17 = registerGTCEuMaterial("expetidalloy_d_17")
                .langValue("Expetidalloy D-17")
                .ingot()
                .fluid()
                .plasma()
                .components(HafnideCeramicBase, 2, HastelloyC276, 11, Dragonsteel, 3,
                        RhodiumPlatedPalladium, 1)
                .color(0xa78e99)
                .secondaryColor(0x948da6)
                .iconSet(SHINY)
                .blast(b -> b.temp(18880, BlastProperty.GasTier.HIGHEST).blastStats(VA[UXV], 600))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_BOLT_SCREW, GENERATE_DENSE,
                        GENERATE_LONG_ROD, GENERATE_GEAR, GENERATE_SMALL_GEAR, DISABLE_DECOMPOSITION,
                        DISABLE_ALLOY_BLAST)
                .buildAndRegister();

        RhenateW = registerGTCEuMaterial("rhenate_w")
                .langValue("Rhenate-W")
                .ingot()
                .fluid()
                .plasma()
                .components(Rhenium, 2, Tungsten, 5, Neutronium, 1,
                        RoseGold, 18, Neodymium, 7)
                .color(0x87bcd0)
                .iconSet(SHINY)
                .blast(b -> b.temp(18880, BlastProperty.GasTier.HIGHEST).blastStats(VA[UXV], 600))
                .cableProperties(V[UIV], 1, 192, false)
                .flags(DISABLE_DECOMPOSITION, DISABLE_ALLOY_BLAST, GENERATE_FINE_WIRE)
                .buildAndRegister();

        DracoAbyssal = registerGTCEuMaterial("draco_abyssal")
                .ingot()
                .fluid()
                .plasma()
                .components(Dragon, 1, Voidic, 1, Dragon, 1)
                .color(0x401e6d)
                .secondaryColor(0x340e4d)
                .iconSet(SHINY)
                .blast(b -> b.temp(18880, BlastProperty.GasTier.HIGHEST).blastStats(VA[UXV], 600))
                .rotorStats(12800, 400, 50.0f, 45000)
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_DENSE, GENERATE_LONG_ROD, GENERATE_GEAR,
                        GENERATE_FOIL, GENERATE_SMALL_GEAR, GENERATE_ROTOR, GENERATE_FINE_WIRE, DISABLE_DECOMPOSITION,
                        DISABLE_ALLOY_BLAST)
                .buildAndRegister();

        HvgaSteel = registerGTCEuMaterial("hvga_steel")
                .langValue("Hyper Voidic Graviton Aborition Steel")
                .ingot()
                .fluid()
                .plasma()
                .components(Signalum, 1, HSSG, 3, DracoAbyssal, 1,
                        HSLASteel, 8, TritanSteel, 3)
                .color(0x280c6c)
                .secondaryColor(0x2561b7)
                .iconSet(SHINY)
                .blast(b -> b.temp(18880, BlastProperty.GasTier.HIGHEST).blastStats(VA[UXV], 600))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_BOLT_SCREW, GENERATE_DENSE,
                        GENERATE_LONG_ROD, GENERATE_GEAR, GENERATE_SMALL_GEAR, DISABLE_DECOMPOSITION,
                        DISABLE_ALLOY_BLAST, GENERATE_FOIL)
                .buildAndRegister();

        MelastriumMox = registerGTCEuMaterial("melastrium_mox")
                .langValue("Melastrium MOX")
                .ingot()
                .fluid()
                .plasma()
                .components(Osmiridium, 2, AstrenalloyNx, 7, Melodium, 3,
                        Potin, 1)
                .color(0x7d486d)
                .secondaryColor(0x4c487d)
                .iconSet(SHINY)
                .blast(b -> b.temp(18880, BlastProperty.GasTier.HIGHEST).blastStats(VA[UXV], 600))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_BOLT_SCREW, GENERATE_DENSE,
                        GENERATE_LONG_ROD, GENERATE_GEAR, GENERATE_SMALL_GEAR, DISABLE_DECOMPOSITION,
                        DISABLE_ALLOY_BLAST)
                .buildAndRegister();

        TrikoductiveNeutroSteel = registerGTCEuMaterial("trikoductive_neutro_steel")
                .langValue("Trikoductive Neutro-steel")
                .ingot()
                .fluid()
                .plasma()
                .components(Isovol, 6, TritanSteel, 5, Estalt, 1,
                        RutheniumTriniumAmericiumNeutronate, 3, Twinite, 2)
                .color(0x908080)
                .secondaryColor(0x6a3f3f)
                .iconSet(SHINY)
                .blast(b -> b.temp(18880, BlastProperty.GasTier.HIGHEST).blastStats(VA[UXV], 600))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_BOLT_SCREW, GENERATE_DENSE,
                        GENERATE_LONG_ROD, GENERATE_GEAR, GENERATE_SMALL_GEAR, DISABLE_DECOMPOSITION,
                        DISABLE_ALLOY_BLAST)
                .buildAndRegister();

        SoulAscendantCuperite = registerGTCEuMaterial("soul_ascendant_cuperite")
                .ingot()
                .fluid()
                .plasma()
                .components(SoulInfused, 12, NickelZincFerrite, 3,
                        Magnalium, 6, NiobiumNitride, 5,
                        MercuryBariumCalciumCuprate, 1)
                .color(0x9ca58b)
                .secondaryColor(0x83805a)
                .iconSet(SHINY)
                .blast(b -> b.temp(18880, BlastProperty.GasTier.HIGHEST).blastStats(VA[UXV], 600))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_BOLT_SCREW, GENERATE_DENSE,
                        GENERATE_LONG_ROD, GENERATE_GEAR, GENERATE_SMALL_GEAR, DISABLE_DECOMPOSITION,
                        DISABLE_ALLOY_BLAST)
                .buildAndRegister();

        MythrotightCarbideSteel = registerGTCEuMaterial("mythrotight_carbide_steel")
                .langValue("Mythrotight-Carbide Steel")
                .ingot()
                .fluid()
                .plasma()
                .components(WatertightSteel, 8, Mythril, 2,
                        SamariumIronArsenicOxide, 5, TungstenCarbide, 3, Kanthal, 1)
                .color(0x2b4951)
                .secondaryColor(0x37265e)
                .iconSet(SHINY)
                .blast(b -> b.temp(18880, BlastProperty.GasTier.HIGHEST).blastStats(VA[UXV], 600))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_BOLT_SCREW, GENERATE_DENSE,
                        GENERATE_LONG_ROD, GENERATE_GEAR, GENERATE_SMALL_GEAR, DISABLE_DECOMPOSITION,
                        DISABLE_ALLOY_BLAST)
                .buildAndRegister();

        AerorelientSteel = registerGTCEuMaterial("aerorelient_steel")
                .ingot()
                .fluid()
                .plasma()
                .components(CobaltBrass, 3, RedSteel, 6, WatertightSteel, 2,
                        HSSE, 5, Indium, 1)
                .color(0x6e644d)
                .secondaryColor(0x50583e)
                .iconSet(SHINY)
                .blast(b -> b.temp(18880, BlastProperty.GasTier.HIGHEST).blastStats(VA[UXV], 600))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_BOLT_SCREW, GENERATE_DENSE,
                        GENERATE_LONG_ROD, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROTOR, DISABLE_DECOMPOSITION,
                        DISABLE_ALLOY_BLAST)
                .buildAndRegister();

        VastaqalloyCr4200x = registerGTCEuMaterial("vastaqalloy_cr_4200x")
                .langValue("Vastaqalloy CR-4200X")
                .ingot()
                .fluid()
                .plasma()
                .components(ThacoloyNq42x, 5, Stellite100, 4, VanadiumGallium, 2,
                        TungstenSteel, 3, Chromium, 1)
                .color(0x6f7343)
                .secondaryColor(0x534531)
                .iconSet(SHINY)
                .blast(b -> b.temp(18880, BlastProperty.GasTier.HIGHEST).blastStats(VA[UXV], 600))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_BOLT_SCREW, GENERATE_DENSE,
                        GENERATE_LONG_ROD, GENERATE_GEAR, GENERATE_SMALL_GEAR, DISABLE_DECOMPOSITION,
                        DISABLE_ALLOY_BLAST)
                .buildAndRegister();

        MagmadaAlloy = registerGTCEuMaterial("magmada_alloy")
                .ingot()
                .fluid()
                .plasma()
                .components(Adamantine, 4, Neutronium, 1, RTMAlloy, 3)
                .color(0xda8607)
                .iconSet(SHINY)
                .blast(b -> b.temp(17890, BlastProperty.GasTier.HIGHEST).blastStats(VA[UEV], 600))
                .cableProperties(V[UHV], 1, 3, false)
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_BOLT_SCREW, GENERATE_ROUND,
                        GENERATE_LONG_ROD, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_RING, DISABLE_DECOMPOSITION,
                        GENERATE_ROTOR, GENERATE_FINE_WIRE, DISABLE_ALLOY_BLAST)
                .buildAndRegister();

        UltispestalloyCmsh = registerGTCEuMaterial("ultispestalloy_cmsh")
                .langValue("Ultispestalloy C/MSH")
                .ingot()
                .fluid()
                .plasma()
                .components(MagmadaAlloy, 2, Shellite, 3, Ultimet, 15,
                        HastelloyC276, 6, Hafnium, 1)
                .color(0x684e6f)
                .secondaryColor(0x4b1146)
                .iconSet(SHINY)
                .blast(b -> b.temp(18880, BlastProperty.GasTier.HIGHEST).blastStats(VA[UXV], 600))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_BOLT_SCREW, GENERATE_DENSE,
                        GENERATE_LONG_ROD, GENERATE_GEAR, GENERATE_SMALL_GEAR, DISABLE_DECOMPOSITION,
                        DISABLE_ALLOY_BLAST)
                .buildAndRegister();

        ZeroidicTrinateSteel = registerGTCEuMaterial("zeroidic_trinate_steel")
                .langValue("Zeroidic Trinate-steel")
                .ingot()
                .fluid()
                .plasma()
                .components(EnrichedNaquadahTriniumEuropiumDuranide, 3, Zeron100, 7,
                        Xeproda, 1, TritanSteel, 2, Calamatium, 4)
                .color(0x77686f)
                .secondaryColor(0x3d075c)
                .iconSet(SHINY)
                .blast(b -> b.temp(18880, BlastProperty.GasTier.HIGHEST).blastStats(VA[UXV], 600))
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_BOLT_SCREW, GENERATE_DENSE,
                        GENERATE_LONG_ROD, GENERATE_GEAR, GENERATE_SMALL_GEAR, DISABLE_DECOMPOSITION,
                        DISABLE_ALLOY_BLAST)
                .buildAndRegister();
    }
}
