package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.data.chemical.material.properties.BlastProperty;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.*;
import static com.startechnology.start_core.materials.StarTMaterialHelpers.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

public class StarTSuperconductors {

    public static void register() {
        SoulInfused = registerGTCEuMaterial("soul_infused")
                .ingot()
                .fluid()
                .components(Invar, 1, Soul, 2)
                .color(0xcc9966)
                .iconSet(SHINY)
                .cableProperties(V[LV], 4, 0, true)
                .rotorStats(150, 120, 2.0f, 3000)
                .flags(GENERATE_FOIL, GENERATE_GEAR, GENERATE_LONG_ROD, GENERATE_PLATE, GENERATE_ROD, GENERATE_ROTOR,
                        GENERATE_SMALL_GEAR, GENERATE_RING, GENERATE_FRAME, GENERATE_FINE_WIRE)
                .buildAndRegister();

        Signalum = registerGTCEuMaterial("signalum")
                .ingot()
                .fluid()
                .components(Silver, 1, Copper, 3, Redstone, 4)
                .color(0xff3300)
                .iconSet(SHINY)
                .blast(b -> b.temp(1700, BlastProperty.GasTier.LOW).blastStats(VA[MV], 800))
                .cableProperties(V[MV], 16, 0, true)
                .rotorStats(190, 140, 3.0f, 4000)
                .flags(GENERATE_FOIL, GENERATE_GEAR, GENERATE_LONG_ROD, GENERATE_PLATE, GENERATE_ROD, GENERATE_ROTOR,
                        GENERATE_SMALL_GEAR, GENERATE_RING, GENERATE_FRAME, GENERATE_FINE_WIRE)
                .buildAndRegister();

        Lumium = registerGTCEuMaterial("lumium")
                .ingot()
                .fluid()
                .components(Silver, 1, Tin, 3, Glowstone, 2)
                .color(0xffffb3)
                .iconSet(SHINY)
                .blast(b -> b.temp(1700, BlastProperty.GasTier.LOW).blastStats(VA[HV], 1000))
                .cableProperties(V[HV], 16, 0, true)
                .rotorStats(220, 160, 4.0f, 5500)
                .flags(GENERATE_FOIL, GENERATE_GEAR, GENERATE_LONG_ROD, GENERATE_PLATE, GENERATE_ROD, GENERATE_ROTOR,
                        GENERATE_SMALL_GEAR, GENERATE_RING, GENERATE_FRAME, GENERATE_FINE_WIRE)
                .buildAndRegister();

        Enderium = registerGTCEuMaterial("enderium")
                .ingot()
                .fluid()
                .components(Lead, 3, Diamond, 1, EnderPearl, 2)
                .color(0x006666)
                .iconSet(SHINY)
                .blast(b -> b.temp(3500, BlastProperty.GasTier.LOW).blastStats(VA[EV], 1200))
                .cableProperties(V[EV], 32, 0, true)
                .rotorStats(300, 180, 5.0f, 7500)
                .flags(GENERATE_FOIL, GENERATE_GEAR, GENERATE_LONG_ROD, GENERATE_PLATE, GENERATE_ROD, GENERATE_ROTOR,
                        GENERATE_SMALL_GEAR, GENERATE_RING, GENERATE_FRAME, GENERATE_FINE_WIRE)
                .buildAndRegister();

        Shellite = registerGTCEuMaterial("shellite")
                .ingot()
                .fluid()
                .components(BlackBronze, 1, Signalum, 3)
                .color(0x9933ff)
                .iconSet(SHINY)
                .blast(b -> b.temp(4400, BlastProperty.GasTier.MID).blastStats(VA[IV], 1400))
                .cableProperties(V[IV], 64, 0, true)
                .rotorStats(450, 200, 6.0f, 10000)
                .flags(GENERATE_FOIL, GENERATE_GEAR, GENERATE_LONG_ROD, GENERATE_PLATE, GENERATE_ROD, GENERATE_ROTOR,
                        GENERATE_SMALL_GEAR, GENERATE_RING, GENERATE_FRAME, GENERATE_FINE_WIRE)
                .buildAndRegister();

        Twinite = registerGTCEuMaterial("twinite")
                .ingot()
                .fluid()
                .components(ManganesePhosphide, 3, Amethyst, 2, Lumium, 1)
                .color(0xf66999)
                .iconSet(SHINY)
                .blast(b -> b.temp(5300, BlastProperty.GasTier.MID).blastStats(VA[LuV], 1600))
                .cableProperties(V[LuV], 64, 0, true)
                .rotorStats(700, 220, 7.0f, 13000)
                .flags(GENERATE_FOIL, GENERATE_GEAR, GENERATE_LONG_ROD, GENERATE_PLATE, GENERATE_ROD, GENERATE_ROTOR,
                        GENERATE_SMALL_GEAR, GENERATE_RING, GENERATE_FRAME, GENERATE_FINE_WIRE)
                .buildAndRegister();

        Dragonsteel = registerGTCEuMaterial("dragonsteel")
                .ingot()
                .fluid()
                .components(Tungsten, 4, MagnesiumDiboride, 8, Cadmium, 2)
                .color(0x3333cc)
                .iconSet(SHINY)
                .blast(b -> b.temp(7100, BlastProperty.GasTier.HIGH).blastStats(VA[ZPM], 1800))
                .cableProperties(V[ZPM], 96, 0, true)
                .rotorStats(1100, 240, 8.0f, 16500)
                .flags(GENERATE_FOIL, GENERATE_GEAR, GENERATE_LONG_ROD, GENERATE_PLATE, GENERATE_ROD, GENERATE_ROTOR,
                        GENERATE_SMALL_GEAR, GENERATE_RING, GENERATE_FRAME, GENERATE_FINE_WIRE)
                .buildAndRegister();

        Prismalium = registerGTCEuMaterial("prismalium")
                .ingot()
                .fluid()
                .components(Naquadah, 8, MercuryBariumCalciumCuprate, 4,
                        TungstenCarbide, 7)
                .color(0x66ffff)
                .iconSet(SHINY)
                .blast(b -> b.temp(9000, BlastProperty.GasTier.HIGH).blastStats(VA[ZPM], 1800))
                .cableProperties(V[UV], 48, 0, true)
                .rotorStats(1600, 260, 9.0f, 20500)
                .flags(GENERATE_FOIL, GENERATE_GEAR, GENERATE_LONG_ROD, GENERATE_PLATE, GENERATE_ROD, GENERATE_ROTOR,
                        GENERATE_SMALL_GEAR, GENERATE_RING, GENERATE_FRAME, GENERATE_FINE_WIRE)
                .buildAndRegister();

        Melodium = registerGTCEuMaterial("melodium")
                .ingot()
                .fluid()
                .components(UraniumTriplatinum, 2, Electrum, 14, Amethyst, 3,
                        Darmstadtium, 4, Europium, 7)
                .color(0xd9b3ff)
                .iconSet(SHINY)
                .blast(b -> b.temp(10000, BlastProperty.GasTier.HIGHER).blastStats(VA[UV], 2200))
                .cableProperties(V[UV], 128, 0, true)
                .rotorStats(2000, 280, 10.0f, 26000)
                .flags(GENERATE_FOIL, GENERATE_GEAR, GENERATE_LONG_ROD, GENERATE_PLATE, GENERATE_ROD, GENERATE_ROTOR,
                        GENERATE_SMALL_GEAR, GENERATE_RING, GENERATE_FRAME, GENERATE_FINE_WIRE)
                .buildAndRegister();

        Stellarium = registerGTCEuMaterial("stellarium")
                .ingot()
                .fluid()
                .components(Neutronium, 12, Melodium, 4, SamariumIronArsenicOxide, 1)
                .color(0xccffff)
                .iconSet(SHINY)
                .blast(b -> b.temp(10799, BlastProperty.GasTier.HIGHEST).blastStats(VA[UHV], 2400))
                .cableProperties(V[UHV], 192, 0, true)
                .rotorStats(3200, 300, 12.0f, 32000)
                .flags(GENERATE_FOIL, GENERATE_GEAR, GENERATE_LONG_ROD, GENERATE_PLATE, GENERATE_ROD, GENERATE_ROTOR,
                        GENERATE_SMALL_GEAR, GENERATE_RING, GENERATE_FRAME, GENERATE_FINE_WIRE)
                .buildAndRegister();

        AncientRunicalium = registerGTCEuMaterial("ancient_runicalium")
                .ingot()
                .fluid()
                .components(Zapolgium, 5, Stellarium, 18, Zirconium, 8)
                .color(0xfab922)
                .iconSet(SHINY)
                .blast(b -> b.temp(11749, BlastProperty.GasTier.HIGHEST).blastStats(VA[UEV], 3000))
                .cableProperties(V[UEV], 256, 0, true)
                .rotorStats(6400, 320, 15.0f, 38500)
                .flags(GENERATE_FOIL, GENERATE_GEAR, GENERATE_LONG_ROD, GENERATE_PLATE, GENERATE_ROD, GENERATE_ROTOR,
                        GENERATE_SMALL_GEAR, GENERATE_RING, GENERATE_FRAME, GENERATE_FINE_WIRE)
                .buildAndRegister();

        // Solders
        IndiumTinLeadCadmiumSolderingAlloy = registerGTCEuMaterial("indium_tin_lead_cadmium_soldering_alloy")
                .ingot()
                .fluid()
                .components(Indium, 14, Tin, 3, Lead, 2, Cadmium, 1)
                .color(0xa6a6a6)
                .buildAndRegister();

        NaquadatedSolderingAlloy = registerGTCEuMaterial("naquadated_soldering_alloy")
                .ingot()
                .fluid()
                .components(Tin, 3, Indium, 18, Silver, 6, Lutetium, 4,
                        Cerium, 3, Naquadah, 3, Trinium, 1, Lead, 2)
                .color(0x8790a1)
                .buildAndRegister();
    }
}
