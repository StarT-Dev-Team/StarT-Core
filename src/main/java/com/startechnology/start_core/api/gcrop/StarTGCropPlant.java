package com.startechnology.start_core.api.gcrop;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

import java.util.List;

public class StarTGCropPlant {

    public static final String GCROP_RESOURCE_GENOME_NBT_TAG = "gcrop_resource_genome";
    public static final String GCROP_PRODUCTION_GENOME_NBT_TAG = "gcrop_production_genome";
    public static final String GCROP_AUXILIARY_GENOME_NBT_TAG = "gcrop_auxiliary_genome";

    private List<String> resourceGenome;
    private List<String> productionGenome;
    private List<String> auxiliaryGenome;

    /*
     * <Trait>-<DominantCount>
     * 
     * Example:
     * Charred Aa; Tough aa; Vibrant AA; Fluorescent aa;
     * ===>
     * ["Charred-1", "Vibrant-2"]
     */

    public List<String> getResourceGenome() {
        return resourceGenome;
    }

    public List<String> getProductionGenome() {
        return productionGenome;
    }

    public List<String> getAuxiliaryGenome() {
        return auxiliaryGenome;
    }

    public StarTGCropPlant(List<String> resourceGenome, List<String> productionGenome, List<String> auxiliaryGenome) {
        this.resourceGenome = resourceGenome;
        this.productionGenome = productionGenome;
        this.auxiliaryGenome = auxiliaryGenome;
    }

    public StarTGCropPlant(CompoundTag gCropGenomeCompound) {
        ListTag resourceGenomeList = gCropGenomeCompound.getList(GCROP_RESOURCE_GENOME_NBT_TAG, StringTag.TAG_STRING);
        resourceGenomeList.forEach(gene -> this.resourceGenome.add(gene.getAsString()));

        ListTag productionGenomeList = gCropGenomeCompound.getList(GCROP_PRODUCTION_GENOME_NBT_TAG,
                StringTag.TAG_STRING);
        productionGenomeList.forEach(gene -> this.productionGenome.add(gene.getAsString()));

        ListTag auxiliaryGenomeList = gCropGenomeCompound.getList(GCROP_AUXILIARY_GENOME_NBT_TAG, StringTag.TAG_STRING);
        auxiliaryGenomeList.forEach(gene -> this.auxiliaryGenome.add(gene.getAsString()));
    }

    public CompoundTag toCompoundTag() {
        CompoundTag gCropGenomeCompound = new CompoundTag();

        ListTag resourceGenomeList = new ListTag();
        for (String gene : resourceGenome) {
            resourceGenomeList.add(StringTag.valueOf(gene));
        }

        ListTag productionGenomeList = new ListTag();
        for (String gene : productionGenome) {
            productionGenomeList.add(StringTag.valueOf(gene));
        }

        ListTag auxiliaryGenomeList = new ListTag();
        for (String gene : auxiliaryGenome) {
            auxiliaryGenomeList.add(StringTag.valueOf(gene));
        }

        gCropGenomeCompound.put(GCROP_RESOURCE_GENOME_NBT_TAG, resourceGenomeList);
        gCropGenomeCompound.put(GCROP_PRODUCTION_GENOME_NBT_TAG, productionGenomeList);
        gCropGenomeCompound.put(GCROP_AUXILIARY_GENOME_NBT_TAG, auxiliaryGenomeList);

        return gCropGenomeCompound;
    }
}
