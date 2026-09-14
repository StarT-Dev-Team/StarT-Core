package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.gregtechceu.gtceu.api.fluids.FluidState;

import static com.startechnology.start_core.materials.StarTMaterialHelpers.*;
import static com.startechnology.start_core.materials.StarTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.Water;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.DISABLE_DECOMPOSITION;

public class StarTSteams {

    public static void init() {
        WarmSteam = registerStartCoreMaterial("warm_steam")
                .langValue("Warm Steam")
                .gas(new FluidBuilder()
                        .state(FluidState.GAS)
                        .temperature(423)
                        .customStill())
                .flags(DISABLE_DECOMPOSITION)
                .components(Water, 1)
                .buildAndRegister();

        HotSteam = registerStartCoreMaterial("hot_steam")
                .langValue("Hot Steam")
                .gas(new FluidBuilder()
                        .state(FluidState.GAS)
                        .temperature(473)
                        .customStill())
                .flags(DISABLE_DECOMPOSITION)
                .components(Water, 1)
                .buildAndRegister();

        ExtremelyHotSteam = registerStartCoreMaterial("extremely_hot_steam")
                .langValue("Extremely Hot Steam")
                .gas(new FluidBuilder()
                        .state(FluidState.GAS)
                        .temperature(523)
                        .customStill())
                .flags(DISABLE_DECOMPOSITION)
                .components(Water, 1)
                .buildAndRegister();
    }
}
