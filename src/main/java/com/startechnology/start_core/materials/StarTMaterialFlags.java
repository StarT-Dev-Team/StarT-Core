package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlag;
import com.gregtechceu.gtceu.common.data.GTMaterials;

public class StarTMaterialFlags {

    public static final MaterialFlag NO_DUST_BLOCK = new MaterialFlag.Builder("no_dust_block").build();

    public static void init() {
        GTMaterials.Redstone.addFlags(NO_DUST_BLOCK);
        GTMaterials.Glowstone.addFlags(NO_DUST_BLOCK);
    }
}
