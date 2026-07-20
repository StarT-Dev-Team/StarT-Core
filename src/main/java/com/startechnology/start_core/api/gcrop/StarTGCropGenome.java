package com.startechnology.start_core.api.gcrop;

import com.startechnology.start_core.data.gcrops.StarTGCropTraits;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public class StarTGCropGenome {

    public static final String GCROP_RESOURCE_GENOME_NBT_TAG = "gcrop_resource_genome";
    public static final String GCROP_PRODUCTION_GENOME_NBT_TAG = "gcrop_production_genome";
    public static final String GCROP_AUXILIARY_GENOME_NBT_TAG = "gcrop_auxiliary_genome";

    @NotNull
    private List<StarTGCropGene> resourceGenome = new ArrayList<>();
    @NotNull
    private List<StarTGCropGene> productionGenome = new ArrayList<>();
    @NotNull
    private List<StarTGCropGene> auxiliaryGenome = new ArrayList<>();

    public List<StarTGCropGene> getResourceGenome() {
        return resourceGenome;
    }

    public List<StarTGCropGene> getProductionGenome() {
        return productionGenome;
    }

    public List<StarTGCropGene> getAuxiliaryGenome() {
        return auxiliaryGenome;
    }

    public StarTGCropGenome(@NotNull List<StarTGCropGene> resourceGenome,
                            @NotNull List<StarTGCropGene> productionGenome,
                            @NotNull List<StarTGCropGene> auxiliaryGenome) {
        this.resourceGenome = resourceGenome;
        this.productionGenome = productionGenome;
        this.auxiliaryGenome = auxiliaryGenome;
    }

    public boolean hasTrait(StarTGCropTraits.StarTGCropTrait trait) {
        StarTGCropTraits.GenomeType genomeType = trait.genomeType();
        List<StarTGCropGene> necessaryGenome = new ArrayList<>();

        switch (genomeType) {
            case RESOURCE -> necessaryGenome = resourceGenome;
            case PRODUCTION -> necessaryGenome = productionGenome;
            case AUXILIARY-> necessaryGenome = auxiliaryGenome;
        }

        if (necessaryGenome.isEmpty()) return false;

        for (StarTGCropGene gene : necessaryGenome) {
            if (gene.getTrait().equals(trait)) return true;
        }
        return false;
    }

    public boolean hasTrait(String traitName) {
        var trait = StarTGCropTraits.getTrait(traitName);
        if (trait == null) return false;
        return this.hasTrait(trait);
    }

    public StarTGCropGenome(CompoundTag gCropGenomeCompound) {
        ListTag resourceGenomeList = gCropGenomeCompound.getList(GCROP_RESOURCE_GENOME_NBT_TAG, Tag.TAG_STRING);
        resourceGenomeList.forEach(gene -> this.resourceGenome.add(new StarTGCropGene(gene.getAsString())));

        ListTag productionGenomeList = gCropGenomeCompound.getList(GCROP_PRODUCTION_GENOME_NBT_TAG,
                Tag.TAG_STRING);
        productionGenomeList.forEach(gene -> this.productionGenome.add(new StarTGCropGene(gene.getAsString())));

        ListTag auxiliaryGenomeList = gCropGenomeCompound.getList(GCROP_AUXILIARY_GENOME_NBT_TAG, Tag.TAG_STRING);
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
