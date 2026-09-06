package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.gregtechceu.gtceu.api.fluids.FluidState;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.startechnology.start_core.StarTCore;

public class StarTSteams {

    public static Material WarmSteam;
    public static Material HotSteam;
    public static Material ExtremelyHotSteam;

    public static void init() {
        WarmSteam = new Material.Builder(StarTCore.resourceLocation("warm_steam"))
                .langValue("Warm Steam")
                .gas(new FluidBuilder()
                        .state(FluidState.GAS)
                        .temperature(423)
                        .customStill())
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .components(GTMaterials.Hydrogen, 2, GTMaterials.Oxygen, 1)
                .buildAndRegister();

        HotSteam = new Material.Builder(StarTCore.resourceLocation("hot_steam"))
                .langValue("Hot Steam")
                .gas(new FluidBuilder()
                        .state(FluidState.GAS)
                        .temperature(473)
                        .customStill())
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .components(GTMaterials.Hydrogen, 2, GTMaterials.Oxygen, 1)
                .buildAndRegister();

        ExtremelyHotSteam = new Material.Builder(StarTCore.resourceLocation("extremely_hot_steam"))
                .langValue("Extremely Hot Steam")
                .gas(new FluidBuilder()
                        .state(FluidState.GAS)
                        .temperature(523)
                        .customStill())
                .flags(MaterialFlags.DISABLE_DECOMPOSITION)
                .components(GTMaterials.Hydrogen, 2, GTMaterials.Oxygen, 1)
                .buildAndRegister();
    }
}
