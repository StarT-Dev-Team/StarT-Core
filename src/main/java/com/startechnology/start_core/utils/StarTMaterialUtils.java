package com.startechnology.start_core.utils;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;

import static com.gregtechceu.gtceu.common.data.GTMaterials.Iron;
import static com.startechnology.start_core.StarTCore.LOGGER;

public class StarTMaterialUtils {

    /**
     * Converts an id into a Material
     *
     * @param materialId the id of the material (e.g. {@code start_core:black_pigment})
     * @return a Material of your id
     */
    public static Material getMaterial(String materialId) {
        Material newMat = GTCEuAPI.materialManager.getMaterial(materialId);
        if (newMat == null) {
            LOGGER.debug("Error finding material with id: \"{}\"", materialId);
            return Iron; // safe fallback to allow program to continue
        }
        return newMat;
    }
}
