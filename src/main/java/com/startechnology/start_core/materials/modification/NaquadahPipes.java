package com.startechnology.start_core.materials.modification;

import com.gregtechceu.gtceu.api.data.chemical.material.properties.FluidPipeProperties;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.common.data.GTMaterials;

public class NaquadahPipes {

    public static void register() {
        // Prevent crash from KubeJS
        if (!GTMaterials.NaquadahEnriched.hasProperty(PropertyKey.FLUID_PIPE)) {
            GTMaterials.NaquadahEnriched.setProperty(PropertyKey.FLUID_PIPE,
                    new FluidPipeProperties(8000, 500, true, true, true, false));
        }
    }
}
