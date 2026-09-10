package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.fluids.FluidBuilder;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.*;
import static com.startechnology.start_core.materials.StarTMaterialHelpers.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

public class StarTBaseChemicals {

    public static void register() {
        // Chem Groups
        Sulfate = registerGTCEuMaterial("sulfate")
                .dust()
                .components(Sulfur, 1, Oxygen, 4)
                .color(0xd5ba23)
                .buildAndRegister();

        Silicate = registerGTCEuMaterial("silicate")
                .dust()
                .components(Silicon, 1, Oxygen, 4)
                .color(0xc0ba97)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Pyrophosphate = registerGTCEuMaterial("pyrophosphate")
                .dust()
                .components(Phosphorus, 2, Oxygen, 7)
                .color(0xc08b63)
                .buildAndRegister();

        Hydroxide = registerGTCEuMaterial("hydroxide")
                .fluid()
                .components(Oxygen, 1, Hydrogen, 1)
                .color(0xc0d4dd)
                .buildAndRegister();

        Nitrate = registerGTCEuMaterial("nitrate")
                .gas()
                .components(Nitrogen, 1, Oxygen, 3)
                .color(0xdbc365)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        BismuthTritelluride = registerGTCEuMaterial("bismuth_tritelluride")
                .dust()
                .components(Bismuth, 2, Tellurium, 3)
                .color(0xdeb18e)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        // Base Chemicals
        PerchloricAcid = registerGTCEuMaterial("perchloric_acid")
                .fluid()
                .components(Hydrogen, 1, Chlorine, 1, Oxygen, 4)
                .color(0xffe6e6)
                .buildAndRegister();

        CalciumPerchlorate = registerGTCEuMaterial("calcium_perchlorate")
                .dust()
                .components(Calcium, 1, Chlorine, 2, Oxygen, 8)
                .color(0xffff99)
                .buildAndRegister();

        SilicaGel = registerGTCEuMaterial("silica_gel")
                .fluid()
                .components(Chlorine, 1, Hydrogen, 1, Oxygen, 6,
                        Silicon, 1)
                .color(0xe6e6e6)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        CalciumSulfate = registerGTCEuMaterial("calcium_sulfate")
                .dust()
                .components(Calcium, 1, Sulfur, 1, Oxygen, 4)
                .color(0xffbf80)
                .buildAndRegister();

        SodiumOxide = registerGTCEuMaterial("sodium_oxide")
                .dust()
                .components(Sodium, 2, Oxygen, 1)
                .color(0x6666ff)
                .buildAndRegister();

        StrontiumOxide = registerGTCEuMaterial("strontium_oxide")
                .dust()
                .components(Strontium, 1, Oxygen, 1)
                .color(0xffcc99)
                .buildAndRegister();

        TitaniumOxide = registerGTCEuMaterial("titanium_oxide")
                .dust()
                .components(Titanium, 1, Oxygen, 2)
                .color(0xff66cc)
                .buildAndRegister();

        StrontiumTitaniumOxide = registerGTCEuMaterial("strontium_titanium_oxide")
                .langValue("Strongium-Titanium Oxide")
                .dust()
                .components(Strontium, 1, Titanium, 1, Oxygen, 3)
                .color(0xff0000)
                .buildAndRegister();

        CopperChloride = registerGTCEuMaterial("copper_chloride")
                .langValue("Copper(I) Chloride")
                .dust()
                .components(Copper, 1, Chlorine, 1)
                .color(0xfff9e8)
                .buildAndRegister();

        CupricChlorideSolution = registerGTCEuMaterial("cupric_chloride_solution")
                .fluid()
                .components(CopperChloride, 1, HydrochloricAcid, 1)
                .color(0x336600)
                .buildAndRegister();

        HydroiodicAcid = registerGTCEuMaterial("hydroiodic_acid")
                .fluid()
                .components(Hydrogen, 1, Iodine, 1)
                .color(0x906ad6)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        NickelFluoride = registerGTCEuMaterial("nickel_fluoride")
                .dust()
                .components(Nickel, 1, Fluorine, 2)
                .color(0xa7a9a8)
                .iconSet(METALLIC)
                .buildAndRegister();

        CaesiumFluoride = registerGTCEuMaterial("caesium_fluoride")
                .dust()
                .components(Caesium, 1, Fluorine, 1)
                .color(0x969d9b)
                .iconSet(DULL)
                .buildAndRegister();

        BrominePentafluoride = registerGTCEuMaterial("bromine_pentafluoride")
                .fluid()
                .components(Bromine, 1, Fluorine, 5)
                .color(0x8e6565)
                .buildAndRegister();

        Hexafluorobromine = registerGTCEuMaterial("hexafluorobromine")
                .fluid()
                .components(Bromine, 1, Fluorine, 6)
                .color(0x000000)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        CaesiumHexafluorobromine = registerGTCEuMaterial("caesium_hexafluorobromine")
                .fluid()
                .components(Caesium, 1, Hexafluorobromine, 1)
                .color(0x988585)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        HexafluorobromicAcid = registerGTCEuMaterial("hexafluorobromic_acid")
                .fluid()
                .components(Hydrogen, 1, Hexafluorobromine, 1)
                .color(0xa15e5e)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SulfurHexafluoride = registerGTCEuMaterial("sulfur_hexafluoride")
                .dust()
                .components(Sulfur, 1, Fluorine, 6)
                .color(0xc0ba63)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        MagnesiumHydroxide = registerGTCEuMaterial("magnesium_hydroxide")
                .dust()
                .components(Magnesium, 1, Hydroxide, 2)
                .color(0x766b73)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Iron2Hydroxide = registerGTCEuMaterial("iron_2_hydroxide")
                .langValue("Iron II Hydroxide")
                .dust()
                .components(Iron, 1, Hydroxide, 2)
                .color(0x929a98)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SodiumAstatide = registerGTCEuMaterial("sodium_astatide")
                .dust()
                .components(Sodium, 1, Astatine, 1)
                .color(0x5f5076)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SilicicAcid = registerGTCEuMaterial("silicic_acid")
                .fluid()
                .components(Hydrogen, 4, Silicate, 1)
                .color(0xb4bbbe)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SeaborgiumDioxide = registerGTCEuMaterial("seaborgium_dioxide")
                .dust()
                .components(Seaborgium, 1, Oxygen, 2)
                .color(0x12a190)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        HafniumHexachloride = registerGTCEuMaterial("hafnium_hexachloride")
                .dust()
                .components(Hafnium, 1, Chlorine, 6)
                .color(0xa0a8a6)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SilverSulfate = registerGTCEuMaterial("silver_sulfate")
                .dust()
                .components(Silver, 2, Sulfur, 1, Oxygen, 4)
                .color(0xd4cf91)
                .buildAndRegister();

        FleroviumTetrafluoride = registerGTCEuMaterial("flerovium_tetrafluoride")
                .dust()
                .components(Flerovium, 1, Fluorine, 4)
                .color(0x254722)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        PyrophosphoricAcid = registerGTCEuMaterial("pyrophosphoric_acid")
                .fluid()
                .components(Hydrogen, 4, Pyrophosphate, 1)
                .color(0xb3a36d)
                .buildAndRegister();

        PoloniumCarbonate = registerGTCEuMaterial("polonium_carbonate")
                .dust()
                .components(Polonium, 1, Carbon, 1, Oxygen, 3)
                .color(0x2f5637)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        IndiumOxide = registerGTCEuMaterial("indium_oxide")
                .dust()
                .components(Indium, 2, Oxygen, 3)
                .color(0xe3d28e)
                .buildAndRegister();

        TungstenDisulfide = registerGTCEuMaterial("tungsten_disulfide")
                .dust()
                .fluid()
                .components(Tungsten, 1, Sulfur, 2)
                .color(0x928897)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        IronTitaniumOxide = registerGTCEuMaterial("iron_titanium_oxide")
                .dust()
                .components(Iron, 3, Titanium, 2, Oxygen, 7)
                .color(0x82229b)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        MagnesiumNitride = registerGTCEuMaterial("magnesium_nitride")
                .fluid()
                .components(Magnesium, 3, Nitrogen, 2)
                .color(0xcc66ff)
                .buildAndRegister();

        // Bromine line
        AcidicBromineSolution = registerGTCEuMaterial("acidic_bromine_solution")
                .liquid()
                .color(0xc49b52)
                .components(Chlorine, 1, Bromine, 1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        ConcentratedBromineSolution = registerGTCEuMaterial("concentrated_bromine_solution")
                .liquid()
                .color(0x91481e)
                .components(Bromine, 2, Chlorine, 1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        HydrogenIodide = registerGTCEuMaterial("hydrogen_iodide")
                .gas()
                .color(0x8187a6)
                .components(Hydrogen, 1, Iodine, 1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        HotBrine = registerGTCEuMaterial("hot_brine")
                .liquid(new FluidBuilder().temperature(320))
                .color(0xbe6026)
                .buildAndRegister();

        HotChlorinatedBrominatedBrine = registerGTCEuMaterial("hot_chlorinated_brominated_brine")
                .liquid(new FluidBuilder().temperature(320))
                .color(0xab765d)
                .components(HotBrine, 1, Chlorine, 1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        HotDebrominatedBrine = registerGTCEuMaterial("hot_debrominated_brine")
                .liquid(new FluidBuilder().temperature(320))
                .color(0xab896d)
                .buildAndRegister();

        HotAlkalineDebrominatedBrine = registerGTCEuMaterial("hot_alkaline_debrominated_brine")
                .liquid(new FluidBuilder().temperature(320))
                .color(0xbe8938)
                .components(HotDebrominatedBrine, 2, Chlorine, 1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        RawBrine = registerGTCEuMaterial("raw_brine")
                .liquid()
                .color(0x9f6b26)
                .buildAndRegister();

        DebrominatedBrine = registerGTCEuMaterial("debrominated_brine")
                .liquid()
                .color(0xab8c6d)
                .buildAndRegister();

        BrominatedChlorineVapor = registerGTCEuMaterial("brominated_chlorine_vapor")
                .gas()
                .color(0xbb9b72)
                .components(Chlorine, 1, Bromine, 1, Steam, 1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        AcidicBromineExhaust = registerGTCEuMaterial("acidic_bromine_exhaust")
                .gas()
                .color(0x8f681e)
                .components(Steam, 3, Chlorine, 1)
                .buildAndRegister();

        // Crown Ethers
        SulfurDichloride = registerGTCEuMaterial("sulfur_dichloride")
                .fluid()
                .components(Sulfur, 1, Chlorine, 2)
                .color(0xcc0000)
                .buildAndRegister();

        ThionylChloride = registerGTCEuMaterial("thionyl_chloride")
                .fluid()
                .components(Sulfur, 1, Oxygen, 1, Chlorine, 2)
                .color(0xffffcc)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SulfurylChloride = registerGTCEuMaterial("sulfuryl_chloride")
                .fluid()
                .components(Sulfur, 1, Oxygen, 2, Chlorine, 2)
                .color(0xffffcc)
                .buildAndRegister();

        TriglycolDichloride = registerGTCEuMaterial("triglycol_dichloride")
                .fluid()
                .components(Carbon, 6, Hydrogen, 12, Oxygen, 2,
                        Chlorine, 2)
                .color(0xffffcc)
                .buildAndRegister();

        EthyleneGlycol = registerGTCEuMaterial("ethylene_glycol")
                .fluid()
                .components(Carbon, 2, Hydrogen, 6, Oxygen, 2)
                .color(0xf2f2f2)
                .buildAndRegister();

        DiethyleneGlycol = registerGTCEuMaterial("diethylene_glycol")
                .fluid()
                .components(Carbon, 4, Hydrogen, 10, Oxygen, 3)
                .color(0xf2f2f2)
                .buildAndRegister();

        TriethyleneGlycol = registerGTCEuMaterial("triethylene_glycol")
                .fluid()
                .components(Carbon, 6, Hydrogen, 14, Oxygen, 4)
                .color(0xf2f2f2)
                .buildAndRegister();

        EthyleneOxide = registerGTCEuMaterial("ethylene_oxide")
                .fluid()
                .components(Carbon, 2, Hydrogen, 4, Oxygen, 1)
                .color(0xd9d9d9)
                .buildAndRegister();

        LithiumPerchlorate = registerGTCEuMaterial("lithium_perchlorate")
                .dust()
                .components(Lithium, 1, Chlorine, 1, Oxygen, 4)
                .color(0xe6f2ff)
                .buildAndRegister();

        SodiumPerchlorate = registerGTCEuMaterial("sodium_perchlorate")
                .dust()
                .components(Sodium, 1, Chlorine, 1, Oxygen, 4)
                .color(0xccf2ff)
                .buildAndRegister();

        SodiumChlorate = registerGTCEuMaterial("sodium_chlorate")
                .dust()
                .components(Sodium, 1, Chlorine, 1, Oxygen, 3)
                .color(0xccf2ff)
                .buildAndRegister();

        SilverOxide = registerGTCEuMaterial("silver_oxide")
                .dust()
                .components(Silver, 2, Oxygen, 1)
                .color(0xe3e3e3)
                .buildAndRegister();

        Crown12_4 = registerGTCEuMaterial("12_crown_4")
                .langValue("12-Crown-4")
                .fluid()
                .components(Carbon, 8, Hydrogen, 16, Oxygen, 4)
                .color(0xcc6699)
                .buildAndRegister();

        Crown15_5 = registerGTCEuMaterial("15_crown_5")
                .langValue("15-Crown-5")
                .fluid()
                .components(Carbon, 10, Hydrogen, 20, Oxygen, 5)
                .color(0x0099cc)
                .buildAndRegister();

        Crown18_6 = registerGTCEuMaterial("18_crown_6")
                .langValue("18-crown-6")
                .fluid()
                .components(Carbon, 12, Hydrogen, 24, Oxygen, 6)
                .color(0x99ff33)
                .buildAndRegister();

        Crown12_4_Li = registerGTCEuMaterial("12_crown_4_li")
                .langValue("12-Crown-4 (Li)")
                .fluid()
                .components(Lithium, 1, Carbon, 8, Hydrogen, 16, Oxygen,
                        4)
                .color(0x993366)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Crown15_5_Na = registerGTCEuMaterial("15_crown_5_na")
                .langValue("15-Crown-5 (Na)")
                .fluid()
                .components(Sodium, 1, Carbon, 10, Hydrogen, 20, Oxygen,
                        5)
                .color(0x006080)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Crown18_6_K = registerGTCEuMaterial("18_crown_6_k")
                .langValue("18-Crown-6 (K)")
                .fluid()
                .components(Potassium, 1, Carbon, 12, Hydrogen, 24,
                        Oxygen, 6)
                .color(0x4d9900)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        ToluenesulfonylChloride4 = registerGTCEuMaterial("4_toluenesulfonyl_chloride")
                .langValue("4-Toluenesulfonyl Chloride")
                .dust()
                .components(Carbon, 7, Hydrogen, 7, Chlorine, 2, Oxygen,
                        2, Sulfur, 1)
                .color(0x0ffccc)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        TriethyleneGlycolDitosylate = registerGTCEuMaterial("triethylene_glycol_ditosylate")
                .dust()
                .components(Carbon, 20, Hydrogen, 26, Oxygen, 8, Sulfur,
                        2)
                .color(0xb8b894)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SodiumAzide = registerGTCEuMaterial("sodium_azide")
                .dust()
                .components(Sodium, 1, Nitrogen, 3)
                .color(0xcc6699)
                .buildAndRegister();

        PalladiumOnCarbon = registerGTCEuMaterial("palladium_on_carbon")
                .langValue("Palladium on Carbon")
                .dust()
                .components(Palladium, 1, Carbon, 1)
                .color(0xff9900)
                .buildAndRegister();

        SodiumPToluenesulfonate = registerGTCEuMaterial("sodium_p_toluenesulfonate")
                .langValue("Sodium p-Toluenesulfonate")
                .dust()
                .components(Carbon, 7, Hydrogen, 7, Sodium, 1, Oxygen,
                        3, Sulfur, 1)
                .color(0x00cc00)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        TriethyleneGlycolDiazide = registerGTCEuMaterial("triethylene_glycol_diazide")
                .dust()
                .components(Carbon, 6, Hydrogen, 12, Oxygen, 2,
                        Nitrogen, 6)
                .color(0x6666ff)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        TriethyleneGlycolDiamine = registerGTCEuMaterial("triethylene_glycol_diamine")
                .dust()
                .components(Carbon, 6, Hydrogen, 16, Oxygen, 2,
                        Nitrogen, 2)
                .color(0xcc00cc)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Cryptand = registerGTCEuMaterial("cryptand")
                .langValue("2.2.2-Cryptand")
                .fluid()
                .components(Carbon, 18, Hydrogen, 36, Oxygen, 6,
                        Nitrogen, 2)
                .color(0x993333)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        CryptandK = registerGTCEuMaterial("cryptand_k")
                .langValue("2.2.2-Cryptand (K)")
                .fluid()
                .components(Potassium, 1, Carbon, 18, Hydrogen, 36,
                        Oxygen, 6, Nitrogen, 2)
                .color(0x602020)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        CryptandNa = registerGTCEuMaterial("cryptand_na")
                .langValue("2.2.2-Cryptand (Na)")
                .fluid()
                .components(Sodium, 1, Carbon, 18, Hydrogen, 36, Oxygen,
                        6, Nitrogen, 2)
                .color(0x602020)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        CryptandLi = registerGTCEuMaterial("cryptand_li")
                .langValue("2.2.2-Cryptand (Li)")
                .fluid()
                .components(Lithium, 1, Carbon, 18, Hydrogen, 36,
                        Oxygen, 6, Nitrogen, 2)
                .color(0x602020)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        // Deionized Water Line
        PurifiedWater = registerGTCEuMaterial("purified_water")
                .fluid()
                .components(Hydrogen, 2, Oxygen, 1)
                .color(0x4a94ff)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        AcidicWater = registerGTCEuMaterial("acidic_water")
                .fluid()
                .components(Hydrogen, 2, Oxygen, 1)
                .color(0x2e85ff)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Divinylbenzene = registerGTCEuMaterial("divinylbenzene")
                .fluid()
                .components(Carbon, 10, Hydrogen, 10)
                .color(0x9fb1b8)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        DeionizedWater = registerGTCEuMaterial("deionized_water")
                .langValue("De-ionized Water")
                .fluid()
                .components(Hydrogen, 2, Oxygen, 1)
                .color(0x006aff)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        // Silicon Carbide over Bismuth
        SodiumBorohydride = registerGTCEuMaterial("sodium_borohydride")
                .dust()
                .components(Sodium, 1, Boron, 1, Hydrogen, 4)
                .color(0xe3dec8)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Bismuth3Nitrate = registerGTCEuMaterial("bismuth_3_nitrate")
                .langValue("Bismuth (III) Nitrate")
                .dust()
                .components(Bismuth, 1, Nitrate, 3)
                .color(0xdedbcd)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SodiumNitrate = registerGTCEuMaterial("sodium_nitrate")
                .dust()
                .components(Sodium, 1, Nitrogen, 1, Oxygen, 3)
                .color(0xe6e5e5)
                .buildAndRegister();

        Diborane = registerGTCEuMaterial("diborane")
                .langValue("Diborane (6)")
                .gas()
                .components(Boron, 2, Hydrogen, 6)
                .color(0xfdffe1)
                .buildAndRegister();

        SiliconCarbide = registerGTCEuMaterial("silicon_carbide")
                .dust()
                .components(Silicon, 1, Carbon, 1)
                .color(0xb79f8d)
                .buildAndRegister();

        SiliconCarbideOverBismuthTritelluride = registerGTCEuMaterial("silicon_carbide_over_bismuth_tritelluride")
                .langValue("Silicon Carbide Over Bismuth Tritelluride")
                .dust()
                .components(SiliconCarbide, 1, BismuthTritelluride, 1)
                .color(0x86c455)
                .buildAndRegister();

        // Aerogel line
        LinoleicAcid = registerGTCEuMaterial("linoleic_acid")
                .fluid()
                .components(Carbon, 18, Hydrogen, 31, Oxygen, 2)
                .color(0xdbdcdb)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SodiumLinoleate = registerGTCEuMaterial("sodium_linoleate")
                .dust()
                .components(Carbon, 18, Hydrogen, 31, Oxygen, 2, Sodium,
                        1)
                .color(0xe3ffff)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        AerogelSolventMixture = registerGTCEuMaterial("aerogel_solvent_mixture")
                .fluid()
                .color(0x9de4db)
                .buildAndRegister();

        SiliconTetrachloride = registerGTCEuMaterial("silicon_tetrachloride")
                .fluid()
                .components(Silicon, 1, Chlorine, 4)
                .color(0xdcdbdb)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        TetraethylOrthosilicate = registerGTCEuMaterial("tetraethyl_orthosilicate")
                .fluid()
                .components(Silicon, 1, Carbon, 8, Hydrogen, 20, Oxygen,
                        4)
                .color(0xdbdbdb)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        AerogelPrecursorSolution = registerGTCEuMaterial("aerogel_precursor_solution")
                .fluid()
                .color(0xaebbbf)
                .buildAndRegister();

        Aerogel = registerGTCEuMaterial("aerogel")
                .polymer()
                .ingot()
                .components(Air, 1)
                .color(0x9fcad9)
                .secondaryColor(0xe5f1ee)
                .flags(GENERATE_FOIL, GENERATE_PLATE)
                .buildAndRegister();
    }
}
