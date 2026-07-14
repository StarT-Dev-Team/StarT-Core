package com.startechnology.start_core.api.gcrop;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public class StarTGCropPlant {

    public static final String GCROP_RESOURCE_GENOME_NBT_TAG = "gcrop_resource_genome";
    public static final String GCROP_PRODUCTION_GENOME_NBT_TAG = "gcrop_production_genome";
    public static final String GCROP_AUXILIARY_GENOME_NBT_TAG = "gcrop_auxiliary_genome";

    @NotNull
    private List<StarTGCropGene> resourceGenome;
    @NotNull
    private List<StarTGCropGene> productionGenome;
    @NotNull
    private List<StarTGCropGene> auxiliaryGenome;

    public List<StarTGCropGene> getResourceGenome() {
        return resourceGenome;
    }

    public List<StarTGCropGene> getProductionGenome() {
        return productionGenome;
    }

    public List<StarTGCropGene> getAuxiliaryGenome() {
        return auxiliaryGenome;
    }

    public StarTGCropPlant(@NotNull List<StarTGCropGene> resourceGenome, @NotNull List<StarTGCropGene> productionGenome,
                           @NotNull List<StarTGCropGene> auxiliaryGenome) {
        this.resourceGenome = resourceGenome;
        this.productionGenome = productionGenome;
        this.auxiliaryGenome = auxiliaryGenome;
    }

    public StarTGCropPlant(CompoundTag gCropGenomeCompound) {
        this.resourceGenome = new ArrayList<>();
        this.productionGenome = new ArrayList<>();
        this.auxiliaryGenome = new ArrayList<>();
        ListTag resourceGenomeList = gCropGenomeCompound.getList(GCROP_RESOURCE_GENOME_NBT_TAG, Tag.TAG_LIST);
        resourceGenomeList.forEach(gene -> this.resourceGenome.add(new StarTGCropGene(gene.getAsString())));

        ListTag productionGenomeList = gCropGenomeCompound.getList(GCROP_PRODUCTION_GENOME_NBT_TAG,
                Tag.TAG_LIST);
        productionGenomeList.forEach(gene -> this.productionGenome.add(new StarTGCropGene(gene.getAsString())));

        ListTag auxiliaryGenomeList = gCropGenomeCompound.getList(GCROP_AUXILIARY_GENOME_NBT_TAG, Tag.TAG_LIST);
        auxiliaryGenomeList.forEach(gene -> this.auxiliaryGenome.add(new StarTGCropGene(gene.getAsString())));
    }

    public CompoundTag toCompoundTag() {
        CompoundTag gCropGenomeCompound = new CompoundTag();

        ListTag resourceGenomeList = new ListTag();
        for (StarTGCropGene gene : resourceGenome) {
            resourceGenomeList.add(StringTag.valueOf(gene.toRawGene()));
        }

        ListTag productionGenomeList = new ListTag();
        for (StarTGCropGene gene : productionGenome) {
            productionGenomeList.add(StringTag.valueOf(gene.toRawGene()));
        }

        ListTag auxiliaryGenomeList = new ListTag();
        for (StarTGCropGene gene : auxiliaryGenome) {
            auxiliaryGenomeList.add(StringTag.valueOf(gene.toRawGene()));
        }

        gCropGenomeCompound.put(GCROP_RESOURCE_GENOME_NBT_TAG, resourceGenomeList);
        gCropGenomeCompound.put(GCROP_PRODUCTION_GENOME_NBT_TAG, productionGenomeList);
        gCropGenomeCompound.put(GCROP_AUXILIARY_GENOME_NBT_TAG, auxiliaryGenomeList);

        return gCropGenomeCompound;
    }
}
