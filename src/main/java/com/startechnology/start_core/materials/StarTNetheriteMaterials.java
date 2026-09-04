package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.data.chemical.material.properties.BlastProperty;
import com.startechnology.start_core.elements.StarTElements;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.*;
import static com.startechnology.start_core.materials.StarTMaterialHelpers.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

public class StarTNetheriteMaterials {

    public static void register() {
        // Netherite line
        Debris = registerGTCEuMaterial("debris")
                .dust()
                .fluid()
                .element(StarTElements.Debris)
                .color(0x804000)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        PurifiedDebris = registerGTCEuMaterial("purified_debris")
                .dust()
                .components(Debris, 1)
                .color(0xcc0000)
                .buildAndRegister();

        ChlorineTrifluoride = registerGTCEuMaterial("chlorine_trifluoride")
                .fluid()
                .components(Chlorine, 1, Fluorine, 3)
                .color(0xb3ff99)
                .buildAndRegister();

        Tetrachloroethylene = registerGTCEuMaterial("tetrachloroethylene")
                .fluid()
                .components(Carbon, 2, Chlorine, 4)
                .color(0xd966ff)
                .buildAndRegister();

        AncientDebris = registerGTCEuMaterial("ancient_debris")
                .dust()
                .fluid()
                .components(Mystery, 1)
                .color(0x603d1a)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        AncientNetherite = registerGTCEuMaterial("ancient_netherite")
                .ingot()
                .fluid()
                .element(StarTElements.AncientNetherite)
                .color(0x46271b)
                .iconSet(DULL)
                .blast(b -> b.temp(12349, BlastProperty.GasTier.LOW).blastStats(VA[UEV], 1200))
                .flags(GENERATE_PLATE, GENERATE_ROD, DISABLE_DECOMPOSITION, GENERATE_DENSE, GENERATE_FRAME,
                        GENERATE_LONG_ROD, GENERATE_FOIL)
                .buildAndRegister();

        // Netherite alloys & derivatives
        PureNetherite = registerGTCEuMaterial("pure_netherite")
                .ingot()
                .fluid()
                .element(StarTElements.PureNetherite)
                .color(0x1a0d00)
                .iconSet(DULL)
                .blast(b -> b.temp(5000, BlastProperty.GasTier.LOW).blastStats(VA[IV], 1200))
                .flags(GENERATE_FOIL, GENERATE_GEAR, GENERATE_LONG_ROD, GENERATE_PLATE, GENERATE_ROD, GENERATE_ROTOR,
                        GENERATE_SMALL_GEAR, GENERATE_RING)
                .buildAndRegister();

        NaquadicNetherite = registerGTCEuMaterial("naquadic_netherite")
                .gem()
                .components(Naquadah, 3, PureNetherite, 5, Caesium, 2, Cerium, 5,
                        Fluorine, 12, Oxygen, 32)
                .color(0xffd966)
                .iconSet(DIAMOND)
                .buildAndRegister();

        Trinaquadalloy = registerGTCEuMaterial("trinaquadalloy")
                .ingot()
                .fluid()
                .components(Trinium, 5, Naquadah, 3, Carbon, 2)
                .color(0x281832)
                .iconSet(BRIGHT)
                .blast(b -> b.temp(8750, BlastProperty.GasTier.HIGHER).blastStats(VA[ZPM], 1000))
                .flags(GENERATE_PLATE, GENERATE_ROD, GENERATE_FRAME, GENERATE_FINE_WIRE, GENERATE_FOIL, GENERATE_DENSE)
                .buildAndRegister();

        WeaponGradeNaquadah = registerGTCEuMaterial("weapon_grade_naquadah")
                .ingot()
                .fluid()
                .components(Naquadria, 7, PureNetherite, 4, Trinaquadalloy, 6, Fluorine, 12)
                .color(0xccff33)
                .iconSet(DULL)
                .blast(b -> b.temp(9001, BlastProperty.GasTier.HIGHEST).blastStats(VA[ZPM], 2400))
                .flags(GENERATE_FOIL, GENERATE_PLATE, GENERATE_ROD, GENERATE_FRAME)
                .buildAndRegister();

        StellarizedWeaponGradeNaquadah = registerGTCEuMaterial("stellarized_weapon_grade_naquadah")
                .ingot()
                .fluid()
                .components(Void, 1, WeaponGradeNaquadah, 8, Stellarium,
                        4)
                .color(0x57ab6b)
                .iconSet(SHINY)
                .blast(b -> b.temp(12049, BlastProperty.GasTier.HIGHEST).blastStats(VA[UHV], 3600))
                .flags(GENERATE_FOIL, GENERATE_PLATE, GENERATE_ROD, GENERATE_FRAME)
                .buildAndRegister();

        RunicLaserSourceBase = registerGTCEuMaterial("runic_laser_source_base")
                .gem()
                .components(NaquadicNetherite, 6, Neptunium, 6, Trinium, 5)
                .color(0x00ff00)
                .iconSet(OPAL)
                .buildAndRegister();

        AkreyriadicRunixium = registerGTCEuMaterial("akreyriadic_runixium")
                .gem()
                .components(RunicLaserSourceBase, 7, AncientRunicalium, 4,
                        StrontiumTitaniumOxide, 2, Akreyrium, 5)
                .color(0xffba75)
                .iconSet(OPAL)
                .buildAndRegister();

        // Runic Convergence Infusion Line
        NetheriteTrisulfateComplex = registerGTCEuMaterial("netherite_trisulfate_complex")
                .fluid()
                .components(PureNetherite, 1, Sulfur, 3, Oxygen, 12,
                        Hydroxide, 2)
                .color(0x660033)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        NetheriteHexammineSulfate = registerGTCEuMaterial("netherite_hexammine_sulfate")
                .dust()
                .components(PureNetherite, 1, Ammonia, 6, Sulfur, 1, Oxygen, 4)
                .color(0x400080)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        VoidicNitride = registerGTCEuMaterial("voidic_nitride")
                .fluid()
                .components(PureNetherite, 2, Nitrogen, 3, Oxygen, 4)
                .color(0x000066)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        NetheriteTetrahydroxide = registerGTCEuMaterial("netherite_tetrahydroxide")
                .dust()
                .components(PureNetherite, 1, Hydroxide, 4)
                .color(0x8b8b8b)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        AstralFluorosilicate = registerGTCEuMaterial("astral_fluorosilicate")
                .fluid()
                .components(PureNetherite, 1, Fluorine, 1, Silicon, 2, Oxygen, 4)
                .color(0x333300)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        PrimordialNitrosilicate = registerGTCEuMaterial("primordial_nitrosilicate")
                .fluid()
                .components(PureNetherite, 3, Nitrogen, 3, Silicon, 2, Oxygen, 8,
                        Fluorine, 1)
                .color(0x990099)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        RunicConvergenceInfusion = registerGTCEuMaterial("runic_convergence_infusion")
                .fluid()
                .components(PureNetherite, 3, Magnesium, 6, Nitrogen, 7, Silicon, 2,
                        Oxygen, 8, Fluorine, 1)
                .color(0xcc0099)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();
    }
}
