package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.fluids.FluidBuilder;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.startechnology.start_core.materials.StarTMaterialHelpers.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

public class StarTCombustionMaterials {

    public static void register() {
        Kerosene = registerGTCEuMaterial("kerosene")
                .fluid()
                .components(Carbon, 12, Hydrogen, 26)
                .color(0xe8d44d)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        HydrofinedKerosene = registerGTCEuMaterial("hydrofined_kerosene")
                .fluid()
                .components(Carbon, 12, Hydrogen, 26)
                .color(0xf2ec9a)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Rp1 = registerGTCEuMaterial("rp_1")
                .langValue("RP-1")
                .fluid()
                .components(Carbon, 12, Hydrogen, 26)
                .color(0xf2ec9a)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        LiquidFluorine = registerGTCEuMaterial("liquid_fluorine")
                .liquid(new FluidBuilder().temperature(85))
                .components(Fluorine, 2)
                .color(0xb5e0ff)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        DioxygenDifluoride = registerGTCEuMaterial("dioxygen_difluoride")
                .liquid(new FluidBuilder().temperature(110))
                .components(Oxygen, 2, Fluorine, 2)
                .color(0xe8f1ff)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        WhiteFumingNitricAcid = registerGTCEuMaterial("white_fuming_nitric_acid")
                .fluid()
                .components(Hydrogen, 1, Nitrogen, 1, Oxygen, 3)
                .color(0xfdfefc)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        RedFumingNitricAcid = registerGTCEuMaterial("red_fuming_nitric_acid")
                .fluid()
                .components(Hydrogen, 1, Nitrogen, 1, Oxygen, 3)
                .color(0xff0000)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Monomethylhydrazine = registerGTCEuMaterial("monomethylhydrazine")
                .fluid()
                .components(Carbon, 1, Hydrogen, 6, Nitrogen, 2)
                .color(0x9e9e9e)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        SorbitolHypergolicFuel = registerGTCEuMaterial("sorbitol_hypergolic_fuel")
                .fluid()
                .components(Carbon, 6, Hydrogen, 20, Oxygen, 6,
                        Nitrogen, 1, Boron, 1)
                .color(0xf5f5f5)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        FerroceniumSuperoxide = registerGTCEuMaterial("ferrocenium_superoxide")
                .fluid()
                .components(Carbon, 15, Hydrogen, 5, Fluorine, 15, Iron,
                        1, Oxygen, 2)
                .color(0xb87333)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        FumingNitricAcid = registerGTCEuMaterial("fuming_nitric_acid")
                .fluid()
                .components(Hydrogen, 1, Nitrogen, 1, Oxygen, 3)
                .color(0xf0f0f0)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Hydrazine = registerGTCEuMaterial("hydrazine")
                .fluid()
                .components(Nitrogen, 2, Hydrogen, 4)
                .color(0xc8c8c8)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        AmmoniaBorane = registerGTCEuMaterial("ammonia_borane")
                .dust()
                .components(Nitrogen, 1, Boron, 1, Hydrogen, 6)
                .color(0xffffff)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Ferrocene = registerGTCEuMaterial("ferrocene")
                .fluid()
                .components(Carbon, 10, Hydrogen, 10, Iron, 1)
                .color(0xff8c00)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        HexafluorophosphoricAcid = registerGTCEuMaterial("hexafluorophosphoric_acid")
                .fluid()
                .components(Hydrogen, 1, Phosphorus, 1, Fluorine, 6)
                .color(0xe8e8ff)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        IronCyclopentadienylDichlorobenzene = registerGTCEuMaterial("iron_cyclopentadienyl_dichlorobenzene")
                .fluid()
                .components(Carbon, 16, Hydrogen, 14, Iron, 1, Chlorine,
                        2, Phosphorus, 1, Fluorine, 6)
                .color(0xcc6600)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        FluorinatedFerrocene = registerGTCEuMaterial("fluorinated_ferrocene")
                .fluid()
                .components(Carbon, 15, Hydrogen, 5, Fluorine, 15, Iron,
                        1)
                .color(0x00aa00)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();
    }
}
