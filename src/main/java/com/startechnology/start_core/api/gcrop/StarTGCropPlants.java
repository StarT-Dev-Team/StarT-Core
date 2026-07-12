package com.startechnology.start_core.api.gcrop;

import net.minecraft.nbt.CompoundTag;

public class StarTGCropPlants {
    public static final String GCROP_RESOURCE_GENOME_NBT_TAG = "gcrop_resource_genome";
    public static final String GCROP_PRODUCTION_GENOME_NBT_TAG = "grcop_production_genome";
    public static final String GCROP_AUXILIARY_GENOME_NBT_TAG = "grcop_auxiliary_genome";

    private Integer resourceGenome;
    private Integer productionGenome;
    private Integer auxiliaryGenome;

    public Integer getResourceGenome() {
        return resourceGenome;
    }

    public Integer getProductionGenome() {
        return productionGenome;
    }

    public Integer getAuxiliaryGenome() {
        return auxiliaryGenome;
    }

    public StarTGCropPlants(Integer resourceGenome, Integer metabolism, Integer mutability) {
        this.resourceGenome = resourceGenome;
        this.productionGenome = metabolism;
        this.auxiliaryGenome = mutability;
    }

    public StarTGCropPlants(CompoundTag bacteriaStatsCompound) {
        this.resourceGenome = bacteriaStatsCompound.getInt(GCROP_RESOURCE_GENOME_NBT_TAG);
        this.productionGenome = bacteriaStatsCompound.getInt(GCROP_PRODUCTION_GENOME_NBT_TAG);
        this.auxiliaryGenome = bacteriaStatsCompound.getInt(GCROP_AUXILIARY_GENOME_NBT_TAG);
    }

    public CompoundTag toCompoundTag() {
        CompoundTag bacteriaStatsCompound = new CompoundTag();

        bacteriaStatsCompound.putInt(GCROP_RESOURCE_GENOME_NBT_TAG, this.resourceGenome);
        bacteriaStatsCompound.putInt(GCROP_PRODUCTION_GENOME_NBT_TAG, this.productionGenome);
        bacteriaStatsCompound.putInt(GCROP_AUXILIARY_GENOME_NBT_TAG, this.auxiliaryGenome);

        return bacteriaStatsCompound;
    }
}
