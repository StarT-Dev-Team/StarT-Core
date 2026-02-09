package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.HazardProperty;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.gregtechceu.gtceu.common.data.GTElements;
import com.gregtechceu.gtceu.common.data.GTMedicalConditions;
import com.startechnology.start_core.StarTCore;

public class StarTBacteriaLiquids {

    public static Material Fermentibacter;
    public static Material Xylopseudomonas;
    public static Material Petrospirillum;
    public static Material Octanivorax;
    public static Material Bituminimonas;
    public static Material Carbanogasibacter;

    public static void register() {
        Fermentibacter = new Material.Builder(StarTCore.resourceLocation("fermentibacter_solvis"))
                .liquid(new FluidBuilder())
                .color(0xffffff)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Xylopseudomonas = new Material.Builder(StarTCore.resourceLocation("xylopseudomonas_creosotica"))
                .liquid(new FluidBuilder())
                .color(0xffffff)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Petrospirillum = new Material.Builder(StarTCore.resourceLocation("petrospirillum_solvans"))
                .liquid(new FluidBuilder())
                .color(0xffffff)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Octanivorax = new Material.Builder(StarTCore.resourceLocation("octanivorax_sorbitolens"))
                .liquid(new FluidBuilder())
                .color(0xffffff)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Bituminimonas = new Material.Builder(StarTCore.resourceLocation("bituminimonas_combustilis"))
                .liquid(new FluidBuilder())
                .color(0xffffff)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .buildAndRegister();

        Carbanogasibacter = new Material.Builder(StarTCore.resourceLocation("carbanogasibacter_volatilis"))
                .liquid(new FluidBuilder())
                .color(0xffffff)
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .buildAndRegister();
    }
}