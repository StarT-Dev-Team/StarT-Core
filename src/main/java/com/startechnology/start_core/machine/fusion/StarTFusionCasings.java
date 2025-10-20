package com.startechnology.start_core.machine.fusion;

import com.gregtechceu.gtceu.api.block.IFusionCasingType;
import com.startechnology.start_core.StarTCore;
import net.minecraft.resources.ResourceLocation;

public enum StarTFusionCasings implements IFusionCasingType {

    AUXILIARY_BOOSTED_FUSION_CASING_MK1("auxiliary_boosted_fusion_casing_mk1", 3),
    AUXILIARY_FUSION_COIL_MK1("auxiliary_fusion_coil_mk1", 3),
    FUSION_CASING_MK4("fusion_casing_mk4", 4),
    ADVANCED_FUSION_COIL("advanced_fusion_coil", 3),
    AUXILIARY_BOOSTED_FUSION_CASING_MK2("auxiliary_boosted_fusion_casing_mk2", 4),
    AUXILIARY_FUSION_COIL_MK2("auxiliary_fusion_coil_mk2", 4);

    private final String name;
    private final int harvestLevel;

    StarTFusionCasings(String name, int harvestLevel) {
        this.name = name;
        this.harvestLevel = harvestLevel;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    @Override
    public ResourceLocation getTexture() {
        return StarTCore.resourceLocation("block/casings/fusion/%s".formatted(this.name));
    }

    @Override
    public int getHarvestLevel() {
        return this.harvestLevel;
    }
}
