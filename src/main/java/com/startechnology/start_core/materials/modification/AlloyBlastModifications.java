package com.startechnology.start_core.materials.modification;

import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.data.recipe.misc.alloyblast.CustomAlloyBlastRecipeProducer;
import com.startechnology.start_core.materials.StarTMaterials;

public class AlloyBlastModifications {

    public static void modifyAlloyBlastCasingMaterials() {
        StarTMaterials.Hydronalium.getProperty(PropertyKey.ALLOY_BLAST)
                .setRecipeProducer(new CustomAlloyBlastRecipeProducer(6, 16, -1));
        StarTMaterials.Tumbaga.getProperty(PropertyKey.ALLOY_BLAST)
                .setRecipeProducer(new CustomAlloyBlastRecipeProducer(6, 16, -1));
    }
}
