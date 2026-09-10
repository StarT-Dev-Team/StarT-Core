package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.fluids.FluidBuilder;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet.*;
import static com.startechnology.start_core.materials.StarTMaterialHelpers.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;

public class StarTOresMagmasMaterials {

    public static void register() {
        Titanite = registerGTCEuMaterial("titanite")
                .dust()
                .ore(2, 1)
                .components(Calcium, 1, Titanium, 1, Silicon, 1, Oxygen,
                        5)
                .color(0x66ffff)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Zapolite = registerGTCEuMaterial("zapolite")
                .dust()
                .ore(2, 1)
                .components(Zapolgium, 2, Iodine, 4, Aluminium, 2,
                        Oxygen, 5)
                .color(0xcc0099)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Xenotime = registerGTCEuMaterial("xenotime")
                .gem()
                .ore(2, 1)
                .components(Yttrium, 1, Sulfate, 1)
                .color(0x948446)
                .iconSet(GEM_VERTICAL)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Lautarite = registerGTCEuMaterial("lautarite")
                .dust()
                .ore(2, 1)
                .components(Calcium, 1, Iodine, 2, Oxygen, 6)
                .color(0x6666ff)
                .buildAndRegister();

        Crookesite = registerGTCEuMaterial("crookesite")
                .dust()
                .ore(2, 1)
                .components(Copper, 7, Thallium, 1, Selenium, 4)
                .color(0x00ff99)
                .buildAndRegister();

        Kitkaite = registerGTCEuMaterial("kitkaite")
                .dust()
                .ore(2, 1)
                .components(Nickel, 1, Tellurium, 1, Selenium, 1)
                .color(0xe6ead3)
                .buildAndRegister();

        Strontianite = registerGTCEuMaterial("strontianite")
                .dust()
                .ore(2, 1)
                .components(Strontium, 1, Carbon, 1, Oxygen, 3)
                .color(0xe6ffff)
                .buildAndRegister();

        Celestine = registerGTCEuMaterial("celestine")
                .dust()
                .ore(2, 1)
                .components(Strontium, 1, Carbon, 1, Oxygen, 4)
                .color(0xe6ffff)
                .buildAndRegister();

        Zavaritskite = registerGTCEuMaterial("zavaritskite")
                .dust()
                .ore(2, 1)
                .components(Bismuth, 1, Oxygen, 1, Fluorine, 1)
                .color(0xe7d795)
                .buildAndRegister();

        Naquadite = registerGTCEuMaterial("naquadite")
                .dust()
                .ore(2, 1)
                .components(Naquadah, 2, Magnesia, 1, Magnetite, 1)
                .color(0x272424)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        AbydosNaquaditeDenseMagma = registerGTCEuMaterial("abydos_naquadite_dense_magma")
                .langValue("Abydos-Naquadite Dense Magma")
                .liquid(new FluidBuilder().temperature(5120))
                .components(Mystery, 1, Naquadite, 1, Mystery, 1)
                .color(0x272424)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        NaquaditeDenseResidue = registerGTCEuMaterial("naquadite_dense_residue")
                .liquid(new FluidBuilder().temperature(2560))
                .components(Mystery, 1, Naquadite, 1, Mystery, 1)
                .color(0x524848)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        AbydosRefractoryDenseMagma = registerGTCEuMaterial("abydos_refractory_dense_magma")
                .langValue("Abydos-Refractory Dense Magma")
                .liquid(new FluidBuilder().temperature(4520))
                .components(Mystery, 1, Titanite, 1, Xenotime, 1, Monazite, 1,
                        Scheelite, 1, Mystery, 1)
                .color(0xe65c00)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        AbydosReactiveDenseMagma = registerGTCEuMaterial("abydos_reactive_dense_magma")
                .langValue("Abydos-Reactivity Dense Magma")
                .liquid(new FluidBuilder().temperature(4980))
                .components(Mystery, 1, Zapolite, 1, Crookesite, 1, Kitkaite, 1, Lautarite, 1,
                        Mystery, 1)
                .color(0xff471a)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        AbydosMagmaSlag = registerGTCEuMaterial("abydos_magma_slag")
                .dust()
                .components(Mystery, 1)
                .color(0x8a726d)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        RefractoryDenseResidue = registerGTCEuMaterial("refractory_dense_residue")
                .liquid(new FluidBuilder().temperature(2370))
                .components(Mystery, 1, Titanite, 1, Xenotime, 1, Monazite, 1,
                        Scheelite, 1, Mystery, 1)
                .color(0xb85513)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();

        ReactiveDenseResidue = registerGTCEuMaterial("reactive_dense_residue")
                .liquid(new FluidBuilder().temperature(2450))
                .components(Mystery, 1, Zapolite, 1, Crookesite, 1, Kitkaite, 1, Lautarite, 1,
                        Mystery, 1)
                .color(0xad2705)
                .flags(DISABLE_DECOMPOSITION)
                .buildAndRegister();
    }
}
