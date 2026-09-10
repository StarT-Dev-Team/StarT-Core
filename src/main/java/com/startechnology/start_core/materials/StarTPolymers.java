package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.data.chemical.material.properties.ToolProperty;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.startechnology.start_core.materials.StarTMaterialHelpers.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

public class StarTPolymers {

    public static void register() {
        // PEDOT:PSS Line
        MaleicAnhydride = registerGTCEuMaterial("maleic_anhydride")
                .fluid()
                .components(Carbon, 4, Hydrogen, 2, Oxygen, 3)
                .color(0xaaa099)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        DimethylMaleate = registerGTCEuMaterial("dimethyl_maleate")
                .fluid()
                .components(Carbon, 6, Hydrogen, 10, Oxygen, 4)
                .color(0xc2bfb7)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        DimethylSuccinate = registerGTCEuMaterial("dimethyl_succinate")
                .fluid()
                .components(Carbon, 4, Hydrogen, 10, Oxygen, 3)
                .color(0xd0ccc4)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Butanediol14 = registerGTCEuMaterial("14_butanediol")
                .langValue("1,4-Butanediol")
                .fluid()
                .components(Carbon, 4, Hydrogen, 10, Oxygen, 2)
                .color(0xb8c4c4)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Thiophene = registerGTCEuMaterial("thiophene")
                .dust()
                .components(Carbon, 4, Hydrogen, 4, Sulfur, 1)
                .color(0xc8b680)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Dibromoethane12 = registerGTCEuMaterial("12_dibromoethane")
                .langValue("1,2-Dibromoethane")
                .fluid()
                .components(Carbon, 2, Hydrogen, 4, Bromine, 2)
                .color(0xb0a6cc)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Dimethylformamide = registerGTCEuMaterial("dimethylformamide")
                .gas()
                .components(Carbon, 3, Hydrogen, 7, Nitrogen, 1, Oxygen,
                        1)
                .color(0xa3b0b7)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Ethylenedioxythiophene34 = registerGTCEuMaterial("34_ethylenedioxythiophene")
                .langValue("3,4-Ethylenedioxythiophene")
                .fluid()
                .components(Carbon, 6, Hydrogen, 6, Oxygen, 2, Sulfur,
                        1)
                .color(0x8a9a86)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        PotassiumBromide = registerGTCEuMaterial("potassium_bromide")
                .dust()
                .components(Potassium, 1, Bromine, 1)
                .color(0xd0d0d0)
                .buildAndRegister();

        BenzoylPeroxide = registerGTCEuMaterial("benzoyl_peroxide")
                .dust()
                .components(Carbon, 14, Hydrogen, 10, Oxygen, 4)
                .color(0xc6a8a8)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        HydrogenChloride = registerGTCEuMaterial("hydrogen_chloride")
                .gas()
                .components(Hydrogen, 1, Chlorine, 1)
                .color(0xa8ccc2)
                .buildAndRegister();

        ChlorosulfonicAcid = registerGTCEuMaterial("chlorosulfonic_acid")
                .fluid()
                .components(Hydrogen, 1, Sulfur, 1, Oxygen, 3, Chlorine,
                        1)
                .color(0xa84e4e)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        PolystyreneSulfonate = registerGTCEuMaterial("polystyrene_sulfonate")
                .fluid()
                .components(Carbon, 8, Hydrogen, 8, Oxygen, 3, Sulfur,
                        1)
                .color(0xd8c6f0)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        FerricNitrate = registerGTCEuMaterial("ferric_nitrate")
                .dust()
                .components(Iron, 1, Nitrate, 3)
                .color(0xaf5f5f)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Poly34Ethylenedioxythiophene = registerGTCEuMaterial("poly_34_ethylenedioxythiophene")
                .langValue("Poly(3,4-Ethylenedioxythiophene)")
                .fluid()
                .components(Carbon, 6, Hydrogen, 4, Oxygen, 2, Sulfur,
                        1)
                .color(0x7c8fb2)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Sorbitol = registerGTCEuMaterial("sorbitol")
                .fluid()
                .components(Carbon, 6, Hydrogen, 14, Oxygen, 6)
                .color(0xf8f0e8)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Sorbitan = registerGTCEuMaterial("sorbitan")
                .fluid()
                .components(Carbon, 6, Hydrogen, 12, Oxygen, 5)
                .color(0xe8d6c6)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SorbitanMonoester = registerGTCEuMaterial("sorbitan_monoester")
                .fluid()
                .components(Carbon, 24, Hydrogen, 46, Oxygen, 6)
                .color(0xd9cbb3)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Polysorbate20 = registerGTCEuMaterial("polysorbate_20")
                .langValue("Polysorbate-20")
                .fluid()
                .components(SorbitanMonoester, 1, EthyleneGlycol, 20)
                .color(0xa0d7dd)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Poly34EthylenedioxythiophenePolystyreneSulfonatePaste = registerGTCEuMaterial(
                "poly_34_ethylenedioxythiophene_polystyrene_sulfonate_paste")
                .langValue("[Poly(3,4-Ethylenedioxythiophene):Polystyrene Sulfonate] Paste")
                .dust()
                .components(Carbon, 9, Hydrogen, 7, Sulfur, 1, Oxygen,
                        3)
                .color(0x5d5a85)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Poly34EthylenedioxythiophenePolystyreneSulfonateSolution = registerGTCEuMaterial(
                "poly_34_ethylenedioxythiophene_polystyrene_sulfonate_solution")
                .langValue("[Poly(3,4-Ethylenedioxythiophene):Polystyrene Sulfonate] Solution")
                .fluid()
                .components(Poly34EthylenedioxythiophenePolystyreneSulfonatePaste, 1, Water, 2)
                .color(0x6c7fb0)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Poly34EthylenedioxythiophenePolystyreneSulfonate = registerGTCEuMaterial(
                "poly_34_ethylenedioxythiophene_polystyrene_sulfonate")
                .langValue("Poly(3,4-Ethylenedioxythiophene):Polystyrene Sulfonate")
                .polymer()
                .fluid()
                .components(Carbon, 8, Hydrogen, 7, Sulfur, 1, Oxygen,
                        3)
                .color(0x26396d)
                .fluidPipeProperties(675, 800, true, true, true, false)
                .flags(GENERATE_FOIL, GENERATE_PLATE, GENERATE_RING, DISABLE_DECOMPOSITION)
                .buildAndRegister();

        // PEEK Line
        DisodiumSaltOfHydroquinone = registerGTCEuMaterial("disodium_salt_of_hydroquinone")
                .langValue("Disodium Salt of Hydroquinone")
                .dust()
                .components(Carbon, 6, Hydrogen, 4, Oxygen, 2, Sodium,
                        2)
                .color(0xeaeaf9)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Hydroquinone = registerGTCEuMaterial("hydroquinone")
                .dust()
                .components(Carbon, 6, Hydrogen, 6, Oxygen, 2)
                .color(0xf9f9ff)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SodiumFluoride = registerGTCEuMaterial("sodium_fluoride")
                .dust()
                .components(Sodium, 1, Fluorine, 1)
                .color(0xdedee2)
                .buildAndRegister();

        CarbonAcid = registerGTCEuMaterial("carbon_acid")
                .gas()
                .components(Hydrogen, 2, Carbon, 1, Oxygen, 3)
                .color(0x333333)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Fluorobenzene = registerGTCEuMaterial("fluorobenzene")
                .fluid()
                .components(Carbon, 6, Hydrogen, 5, Fluorine, 1)
                .color(0xffffff)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        FluorobenzoylChloride4 = registerGTCEuMaterial("4_fluorobenzoyl_chloride")
                .langValue("4-Fluorobenzoyl Chloride")
                .fluid()
                .components(Carbon, 7, Hydrogen, 4, Chlorine, 1,
                        Fluorine, 1, Oxygen, 1)
                .color(0xfffff0)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        BenzoylChloride = registerGTCEuMaterial("benzoyl_chloride")
                .fluid()
                .components(Carbon, 7, Hydrogen, 5, Chlorine, 1, Oxygen,
                        1)
                .color(0xfffadf)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Benzotrichloride = registerGTCEuMaterial("benzotrichloride")
                .fluid()
                .components(Carbon, 7, Hydrogen, 5, Chlorine, 3)
                .color(0xddd8bc)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Difluorobenzophenone44 = registerGTCEuMaterial("44_difluorobenzophenone")
                .langValue("4,4-Difluorobenzophenone")
                .dust()
                .components(Carbon, 13, Hydrogen, 8, Oxygen, 1,
                        Fluorine, 2)
                .color(0xeee1c9)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        PolyetherEtherKetone = registerGTCEuMaterial("polyether_ether_ketone")
                .polymer()
                .fluid()
                .components(Carbon, 19, Hydrogen, 12, Oxygen, 3)
                .color(0xccbba7)
                .fluidPipeProperties(550, 600, true, true, true, false)
                .flags(GENERATE_FOIL, GENERATE_PLATE, GENERATE_RING, DISABLE_DECOMPOSITION)
                .buildAndRegister();

        // Perfluoroelastomer Rubber Line
        PerfluoromethylVinylEther = registerGTCEuMaterial("perfluoromethyl_vinyl_ether")
                .fluid()
                .components(Carbon, 3, Fluorine, 6, Oxygen, 1)
                .color(0xd0e5e5)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Hexafluorobutadiene = registerGTCEuMaterial("hexafluorobutadiene")
                .fluid()
                .components(Carbon, 4, Fluorine, 6)
                .color(0xb8d2d9)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        RawPerfluoroelastomerRubber = registerGTCEuMaterial("raw_perfluoroelastomer_rubber")
                .dust()
                .components(Tetrafluoroethylene, 3, PerfluoromethylVinylEther, 1, Hexafluorobutadiene, 1)
                .color(0xb0cccc)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        PerfluoroelastomerRubber = registerGTCEuMaterial("perfluoroelastomer_rubber")
                .polymer()
                .fluid()
                .components(RawPerfluoroelastomerRubber, 1)
                .color(0x536767)
                .flags(GENERATE_FOIL, GENERATE_PLATE, GENERATE_RING, GENERATE_ROD, DISABLE_DECOMPOSITION)
                .toolStats(ToolProperty.Builder.of(1.0F, 1.0F, 65535, 1, GTToolType.SOFT_MALLET, GTToolType.PLUNGER)
                        .unbreakable().build())
                .buildAndRegister();

        // Polycarbonate Line
        SodiumDiphenoxide = registerGTCEuMaterial("sodium_diphenoxide")
                .dust()
                .components(Sodium, 2, Oxygen, 2, Carbon, 15, Hydrogen,
                        16)
                .color(0xfefefe)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Phosgene = registerGTCEuMaterial("phosgene")
                .fluid()
                .components(Carbon, 1, Oxygen, 1, Chlorine, 2)
                .color(0xfdfefc)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Polycarbonate = registerGTCEuMaterial("polycarbonate")
                .polymer()
                .fluid()
                .components(Oxygen, 3, Carbon, 16, Hydrogen, 16)
                .color(0x202020)
                .fluidPipeProperties(388, 300, true, true, false, false)
                .flags(DISABLE_DECOMPOSITION, GENERATE_FOIL, GENERATE_PLATE)
                .buildAndRegister();

        // Polyimide Line
        Azanide = registerGTCEuMaterial("azanide")
                .fluid()
                .components(Nitrogen, 1, Hydrogen, 2)
                .color(0xbfc7e5)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Benzophenone = registerGTCEuMaterial("benzophenone")
                .fluid()
                .components(Carbon, 13, Hydrogen, 10, Oxygen, 1)
                .color(0xe5d3b5)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Methylamine = registerGTCEuMaterial("methylamine")
                .fluid()
                .components(Carbon, 1, Hydrogen, 5, Nitrogen, 1)
                .color(0xd8d8e5)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Benzophenone3344TetracarboxylicDianhydride = registerGTCEuMaterial(
                "benzophenone_3344_tetracarboxylic_dianhydride")
                .langValue("3,3′,4,4′-Benzophenone Tetracarboxylic Dianhydride")
                .dust()
                .components(Carbon, 17, Hydrogen, 6, Oxygen, 7)
                .color(0xd1b9a3)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        YButyrolactone = registerGTCEuMaterial("y_butyrolactone")
                .langValue("γ-Butyrolactone")
                .fluid()
                .components(Carbon, 4, Hydrogen, 6, Oxygen, 2)
                .color(0xd6e2e2)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        MPhelyenediamine = registerGTCEuMaterial("m_phelyenediamine")
                .langValue("Meta-Phenylenediamine")
                .fluid()
                .components(Carbon, 6, Hydrogen, 4, Azanide, 2)
                .color(0xe2bfc0)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        NMethyl2Pyrrolidone = registerGTCEuMaterial("n_methyl_2_pyrrolidone")
                .langValue("N-Methyl-2-Pyrrolidone")
                .dust()
                .components(Carbon, 5, Hydrogen, 9, Nitrogen, 1, Oxygen,
                        1)
                .color(0xbdc8d8)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        PolyamicAcid = registerGTCEuMaterial("polyamic_acid")
                .fluid()
                .components(Carbon, 17, Hydrogen, 12, Nitrogen, 2,
                        Oxygen, 6)
                .color(0xcbbfa3)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Polyimide = registerGTCEuMaterial("polyimide")
                .polymer()
                .fluid()
                .components(Carbon, 17, Hydrogen, 10, Nitrogen, 2,
                        Oxygen, 4)
                .color(0xd6a970)
                .flags(GENERATE_FOIL, GENERATE_PLATE, GENERATE_ROD, DISABLE_DECOMPOSITION)
                .buildAndRegister();
    }
}
