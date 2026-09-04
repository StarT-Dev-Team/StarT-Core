package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.data.chemical.material.properties.BlastProperty;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.*;
import static com.startechnology.start_core.materials.StarTMaterialHelpers.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

public class StarTAbydosMaterials {

    public static void register() {
        // Abydos Alloys
        Zalloy = registerGTCEuMaterial("zalloy")
                .langValue("Zalloy")
                .ingot()
                .fluid()
                .components(Zapolgium, 3, Duranium, 4, Europium, 2)
                .color(0xff66ff)
                .iconSet(METALLIC)
                .blast(b -> b.temp(10799, BlastProperty.GasTier.HIGHEST).blastStats(VHA[ZPM], 3000))
                .cableProperties(V[UV], 2, 4, false)
                .flags(GENERATE_PLATE, GENERATE_FRAME, GENERATE_ROD, GENERATE_BOLT_SCREW, GENERATE_ROUND,
                        GENERATE_LONG_ROD, GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_RING, GENERATE_DENSE)
                .buildAndRegister();

        ZirconiumSelenideDiiodide = registerGTCEuMaterial("zirconium_selenide_diiodide")
                .langValue("Zirconium Selenide Diiodide")
                .ingot()
                .fluid()
                .components(Zirconium, 1, Selenium, 1, Iodine, 2)
                .color(0x6600cc)
                .iconSet(DULL)
                .blast(b -> b.temp(9600, BlastProperty.GasTier.HIGHER).blastStats(VA[LuV], 3600))
                .cableProperties(V[UHV], 8, 16, false)
                .flags(GENERATE_SPRING)
                .buildAndRegister();

        Zircalloy4 = registerGTCEuMaterial("zircalloy_4")
                .langValue("Zircalloy-4")
                .ingot()
                .fluid()
                .components(Zirconium, 251, Tin, 3, Chromium, 2, Iron, 1)
                .color(0xff9999)
                .iconSet(DULL)
                .blast(b -> b.temp(9100, BlastProperty.GasTier.HIGHER).blastStats(VA[LuV], 1800))
                .flags(GENERATE_GEAR, GENERATE_SMALL_GEAR, GENERATE_ROTOR, GENERATE_ROUND, GENERATE_FRAME)
                .buildAndRegister();

        ThoriumPlutDuranide241 = registerGTCEuMaterial("thorium_plut_duranide_241")
                .langValue("Thorium-Plut-Duranide-241")
                .ingot()
                .fluid()
                .components(Thorium, 4, Duranium, 1, Plutonium241, 3)
                .color(0xec342a)
                .blast(b -> b.temp(10199, BlastProperty.GasTier.HIGHEST).blastStats(VA[UV], 850))
                .flags(GENERATE_FINE_WIRE, DISABLE_DECOMPOSITION, GENERATE_FOIL)
                .buildAndRegister();

        // Zapolgium Line
        ZapolgiumAluminiumOxide = registerGTCEuMaterial("zapolgium_aluminium_oxide")
                .langValue("Zapolgium Aluminium Oxide")
                .dust()
                .components(Zapolgium, 1, Iodine, 2, Aluminium, 2, Oxygen, 4)
                .color(0x6666ff)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        ZapolgiumDiiodideDioxide = registerGTCEuMaterial("zapolgium_diiodide_dioxide")
                .langValue("Zapolgium Diiodide Dioxide")
                .dust()
                .components(Zapolgium, 1, Iodine, 2, Oxygen, 2)
                .color(0x660066)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        ZapolgiumDiiodideOxide = registerGTCEuMaterial("zapolgium_diiodide_oxide")
                .langValue("Zapolgium Diiodide Oxide")
                .dust()
                .components(Zapolgium, 1, Iodine, 2, Oxygen, 1)
                .color(0xff66ff)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        ZapolgiumOxide = registerGTCEuMaterial("zapolgium_oxide")
                .langValue("Zapolgium Oxide")
                .dust()
                .components(Zapolgium, 1, Oxygen, 1)
                .color(0xff9933)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        ZapolgiumChloride = registerGTCEuMaterial("zapolgium_chloride")
                .langValue("Zapolgium Chloride")
                .dust()
                .components(Zapolgium, 1, Chlorine, 2)
                .color(0x99ff33)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        ZapolgiumHydroxide = registerGTCEuMaterial("zapolgium_hydroxide")
                .langValue("Zapolgium Hydroxide")
                .dust()
                .components(Zapolgium, 1, Oxygen, 2, Hydrogen, 2)
                .color(0x00ff99)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        // Zirconium Line
        TitaniteSlurry = registerGTCEuMaterial("titanite_slurry")
                .langValue("Titanite Slurry")
                .fluid()
                .components(Titanite, 1, Mystery, 1)
                .color(0x862d2d)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        TitaniteSlurryResidue = registerGTCEuMaterial("titanite_slurry_residue")
                .langValue("Titanite Slurry Residue")
                .fluid()
                .components(Rutile, 1, Mystery, 1)
                .color(0xbf4040)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        HydroxoDioxoTitaniteMixture = registerGTCEuMaterial("hydroxo_dioxo_titanite_mixture")
                .langValue("Hydroxo Dioxo Titanite Mixture")
                .fluid()
                .components(Sodium, 2, Rutile, 1, Oxygen, 2, Hydrogen, 2, Mystery, 1)
                .color(0xd27979)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        TitaniteResidue = registerGTCEuMaterial("titanite_residue")
                .langValue("Titanite Residue")
                .fluid()
                .components(Rutile, 1, Mystery, 1)
                .color(0xe6004c)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        TitaniumTetrachlorideMixture = registerGTCEuMaterial("titanium_tetrachloride_mixture")
                .langValue("Titanium Tetrachloride Mixture")
                .fluid()
                .components(TitaniumTetrachloride, 1, Mystery, 1)
                .color(0xff1a66)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        ZirconiumTetrachloride = registerGTCEuMaterial("zirconium_tetrachloride")
                .langValue("Zirconium Tetrachloride")
                .dust()
                .components(Zirconium, 1, Chlorine, 4)
                .color(0xffad33)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        // Xenotime Line
        RareEarthSulfateSolution = registerGTCEuMaterial("rare_earth_sulfate_solution")
                .langValue("Rare Earth Sulfate Solution")
                .fluid()
                .components(Mystery, 2, Sulfate, 3)
                .color(0xc6c2a8)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        RareEarthLeachMixture = registerGTCEuMaterial("rare_earth_leach_mixture")
                .langValue("Rare Earth Leach Mixture")
                .fluid()
                .components(Mystery, 3, Sulfate, 3)
                .color(0xafad9f)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        RareEarthConcentrate = registerGTCEuMaterial("rare_earth_concentrate")
                .langValue("Rare Earth Concentrate")
                .fluid()
                .components(Mystery, 1, Sulfur, 1, Oxygen, 4)
                .color(0x8c8a7e)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        RichRareEarth = registerGTCEuMaterial("rich_rare_earth")
                .langValue("Rich Rare Earth")
                .dust()
                .components(Mystery, 1)
                .color(0xb5ac90)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        // Naquadite Line
        NaquaditeSolution = registerGTCEuMaterial("naquadite_solution")
                .langValue("Naquadite Solution")
                .fluid()
                .components(Naquadite, 1, Mystery, 1)
                .color(0x524848)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();
    }
}
