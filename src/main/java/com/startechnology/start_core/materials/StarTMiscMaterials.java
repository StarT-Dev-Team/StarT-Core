package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.startechnology.start_core.elements.StarTElements;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.*;
import static com.startechnology.start_core.materials.StarTMaterialHelpers.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

public class StarTMiscMaterials {

    public static void register() {
        NuclearSteam = registerGTCEuMaterial("nuclear_steam")
                .fluid()
                .components(Steam, 1, Mystery, 1)
                .color(0xcccccc)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        HotSodiumPotassium = registerGTCEuMaterial("hot_sodium_potassium")
                .fluid()
                .components(SodiumPotassium, 1, Mystery, 1)
                .color(0x82fcc3)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        HotPcbCoolant = registerGTCEuMaterial("hot_pcb_coolant")
                .liquid(new FluidBuilder().temperature(373))
                .components(PCBCoolant, 1, Mystery, 1)
                .color(0xc9ca81)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Diatron = registerGTCEuMaterial("diatron")
                .gem()
                .color(0x6699ff)
                .iconSet(LAPIS)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        NpkSolution = registerGTCEuMaterial("npk_solution")
                .fluid()
                .components(Apatite, 15, Potassium, 5, SulfurTrioxide, 1,
                        Nitrogen, 1, Water, 2)
                .color(0xb8c3f5)
                .buildAndRegister();

        PurifiedNaquadah = registerGTCEuMaterial("purified_naquadah")
                .gem()
                .element(StarTElements.PurifiedNaquadah)
                .color(0x000807)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Warped = registerGTCEuMaterial("warped")
                .dust()
                .color(0x4fbb85)
                .flags(NO_SMELTING)
                .buildAndRegister();

        HellfireAsh = registerGTCEuMaterial("hellfire_ash")
                .dust()
                .components(Mystery, 1)
                .color(0x5e4646)
                .flags(NO_SMELTING, DISABLE_DECOMPOSITION)
                .buildAndRegister();

        // Resource gen
        IronMixture = registerGTCEuMaterial("iron_mixture")
                .fluid()
                .components(Mystery, 1)
                .color(0xc42626)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        CopperMixture = registerGTCEuMaterial("copper_mixture")
                .fluid()
                .components(Mystery, 1)
                .color(0xc86524)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        QuartzMixture = registerGTCEuMaterial("quartz_mixture")
                .fluid()
                .components(Mystery, 1)
                .color(0xabc5e0)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        RareOreResidue = registerGTCEuMaterial("rare_ore_residue")
                .fluid()
                .components(Mystery, 1)
                .color(0x556278)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        ChromiteSludge = registerGTCEuMaterial("chromite_sludge")
                .dust()
                .components(Chromite, 2, Mystery, 1)
                .color(0x4c3c4c)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        RareSludge = registerGTCEuMaterial("rare_sludge")
                .dust()
                .components(Mystery, 1)
                .color(0xceec94)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        VanadiumMagnetiteSludge = registerGTCEuMaterial("vanadium_magnetite_sludge")
                .dust()
                .components(VanadiumMagnetite, 2, Mystery, 1)
                .color(0x1c1c2c)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        CobaltiteSludge = registerGTCEuMaterial("cobaltite_sludge")
                .dust()
                .components(Cobaltite, 2, Mystery, 1)
                .color(0x6186bb)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        RareMetallicResidue = registerGTCEuMaterial("rare_metallic_residue")
                .dust()
                .components(Silver, 1, Calcite, 2)
                .color(0x515755)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        RawOreSlurry = registerGTCEuMaterial("raw_ore_slurry")
                .fluid()
                .components(Mystery, 1)
                .color(0x7b8087)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        MixedMineralResidue = registerGTCEuMaterial("mixed_mineral_residue")
                .fluid()
                .components(Mystery, 1)
                .color(0x566e6e)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SulfuricMineralMixture = registerGTCEuMaterial("sulfuric_mineral_mixture")
                .fluid()
                .components(Mystery, 1)
                .color(0xe34f1e)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        OxygenousMineralMixture = registerGTCEuMaterial("oxygenous_mineral_mixture")
                .fluid()
                .components(Mystery, 1)
                .color(0x359696)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        MoltenOreMixture = registerGTCEuMaterial("molten_ore_mixture")
                .liquid(new FluidBuilder().temperature(1273))
                .components(Mystery, 1)
                .color(0x575050)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        MoltenBauxiteOre = registerGTCEuMaterial("molten_bauxite_ore")
                .liquid(new FluidBuilder().temperature(1160))
                .components(Bauxite, 1)
                .color(0xb5b69a)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        MoltenPitchblendeOre = registerGTCEuMaterial("molten_pitchblende_ore")
                .liquid(new FluidBuilder().temperature(1160))
                .components(Pitchblende, 1)
                .color(0xafc585)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        MoltenMolybdeniteOre = registerGTCEuMaterial("molten_molybdenite_ore")
                .liquid(new FluidBuilder().temperature(1160))
                .components(Molybdenite, 1)
                .color(0xc1d0a4)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        MoltenIlmeniteOre = registerGTCEuMaterial("molten_ilmenite_ore")
                .liquid(new FluidBuilder().temperature(1160))
                .components(Ilmenite, 1)
                .color(0xcba88f)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        MoltenTungstateOre = registerGTCEuMaterial("molten_tungstate_ore")
                .liquid(new FluidBuilder().temperature(1160))
                .components(Tungstate, 1)
                .color(0x9cacb1)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        MoltenBastnasiteOre = registerGTCEuMaterial("molten_bastnasite_ore")
                .liquid(new FluidBuilder().temperature(1160))
                .components(Bastnasite, 1)
                .color(0x988e84)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        MoltenCooperiteOre = registerGTCEuMaterial("molten_cooperite_ore")
                .liquid(new FluidBuilder().temperature(1160))
                .components(Cooperite, 1)
                .color(0xa4a38b)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        // Nether star line
        Blitz = registerGTCEuMaterial("blitz")
                .fluid()
                .components(Mystery, 1)
                .color(0xfdf3c4)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Blizz = registerGTCEuMaterial("blizz")
                .fluid()
                .components(Mystery, 1)
                .color(0xb4effa)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Basalz = registerGTCEuMaterial("basalz")
                .fluid()
                .components(Mystery, 1)
                .color(0x6f190e)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        EnergizedBlaze = registerGTCEuMaterial("energized_blaze")
                .fluid()
                .components(Mystery, 1)
                .color(0xffcd1a)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        EnergizedBlitz = registerGTCEuMaterial("energized_blitz")
                .fluid()
                .components(Mystery, 1)
                .color(0xfdf5ce)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        EnergizedBlizz = registerGTCEuMaterial("energized_blizz")
                .fluid()
                .components(Mystery, 1)
                .color(0xb7f0fa)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        EnergizedBasalz = registerGTCEuMaterial("energized_basalz")
                .fluid()
                .components(Mystery, 1)
                .color(0x881f11)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        NetherTemperedBlaze = registerGTCEuMaterial("nether_tempered_blaze")
                .fluid()
                .components(Mystery, 1)
                .color(0xffd333)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        NetherTemperedBlitz = registerGTCEuMaterial("nether_tempered_blitz")
                .fluid()
                .components(Mystery, 1)
                .color(0xfefae7)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        NetherTemperedBlizz = registerGTCEuMaterial("nether_tempered_blizz")
                .fluid()
                .components(Mystery, 1)
                .color(0xcff5fc)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        NetherTemperedBasalz = registerGTCEuMaterial("nether_tempered_basalz")
                .fluid()
                .components(Mystery, 1)
                .color(0x9f2414)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();
    }
}
