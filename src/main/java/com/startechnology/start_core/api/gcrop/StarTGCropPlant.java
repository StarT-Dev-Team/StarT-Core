package com.startechnology.start_core.api.gcrop;

import net.minecraft.nbt.CompoundTag;

public class StarTGCropPlant {
    public static final String GCROP_RESOURCE_GENOME_NBT_TAG = "gcrop_resource_genome";
    public static final String GCROP_PRODUCTION_GENOME_NBT_TAG = "gcrop_production_genome";
    public static final String GCROP_AUXILIARY_GENOME_NBT_TAG = "gcrop_auxiliary_genome";

    // Unsure on this being a string, but CompoundTag doesn't accept an object from what I understand
    private String resourceGenome;
    private String productionGenome;
    private String auxiliaryGenome;

    public String getResourceGenome() {
        return resourceGenome;
    }

    public String getProductionGenome() {
        return productionGenome;
    }

    public String getAuxiliaryGenome() {
        return auxiliaryGenome;
    }

    public StarTGCropPlant(String resourceGenome, String productionGenome, String auxiliaryGenome) {
        this.resourceGenome = resourceGenome;
        this.productionGenome = productionGenome;
        this.auxiliaryGenome = auxiliaryGenome;
    }

    public StarTGCropPlant(CompoundTag gCropGenomeCompound) {
        this.resourceGenome = gCropGenomeCompound.getString(GCROP_RESOURCE_GENOME_NBT_TAG);
        this.productionGenome = gCropGenomeCompound.getString(GCROP_PRODUCTION_GENOME_NBT_TAG);
        this.auxiliaryGenome = gCropGenomeCompound.getString(GCROP_AUXILIARY_GENOME_NBT_TAG);
    }

    public CompoundTag toCompoundTag() {
        CompoundTag gCropGenomeCompound = new CompoundTag();

        gCropGenomeCompound.putString(GCROP_RESOURCE_GENOME_NBT_TAG, this.resourceGenome);
        gCropGenomeCompound.putString(GCROP_PRODUCTION_GENOME_NBT_TAG, this.productionGenome);
        gCropGenomeCompound.putString(GCROP_AUXILIARY_GENOME_NBT_TAG, this.auxiliaryGenome);

        return gCropGenomeCompound;
    }
}
