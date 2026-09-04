package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.startechnology.start_core.StarTCore;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.*;

public class StarTMaterialHelpers {

    /**
     * Registers a new material under the gtceu namespace
     */
    public static Material.Builder registerGTCEuMaterial(String materialName) {
        return new Material.Builder(GTCEu.id(materialName));
    }

    /**
     * Registers a new material under the start_core namespace
     */
    public static Material.Builder registerStartCoreMaterial(String materialName) {
        return new Material.Builder(StarTCore.resourceLocation(materialName));
    }

    public static Material registerGenericDustGT(String name, String langValue, int color, Object... components) {
        return registerGTCEuMaterial(name)
            .langValue(langValue)
            .dust()
            .color(color)
            .components(components)
            .buildAndRegister();
    }

    public static Material registerGenericDustNoDecompGT(String name, String langValue, int color, Object... components) {
        return registerGTCEuMaterial(name)
            .langValue(langValue)
            .dust()
            .color(color)
            .components(components)
            .flags(DISABLE_DECOMPOSITION)
            .buildAndRegister();
    }

    public static Material registerGenericIngotGT(String name, String langValue, int color, Object... components) {
        return registerGTCEuMaterial(name)
            .langValue(langValue)
            .ingot()
            .color(color)
            .components(components)
            .buildAndRegister();
    }
}
